package com.project.samay.presentation.calender

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.samay.data.repository.HistoryRepository
import com.project.samay.domain.model.CalendarColor
import com.project.samay.domain.model.DomainEntity
import com.project.samay.domain.model.CalendarEvent
import com.project.samay.domain.model.CalendarEventStatus
import com.project.samay.domain.model.CalendarType
import com.project.samay.domain.model.DistinctNames
import com.project.samay.domain.model.HistoryEntity
import com.project.samay.domain.repository.CalendarRepository
import com.project.samay.domain.repository.UsageRepository
import com.project.samay.domain.usecases.CalendarScreenUseCases
import com.project.samay.domain.usecases.HistoryUseCases
import com.project.samay.domain.util.CalendarTrackerUtil
import com.project.samay.util.calculations.TimeUtils
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CalendarViewModel(
    private val calendarRepository: CalendarRepository,
    private val calendarScreenUseCases: CalendarScreenUseCases,
    private val historyRepository: HistoryRepository,
    private val historyUseCases: HistoryUseCases,
    private val usageRepository: UsageRepository
) : ViewModel() {
    private var _calendarsList = mutableStateOf(emptyList<CalendarType>())
    val calendarType: State<List<CalendarType>> = _calendarsList
    val allDomains = calendarScreenUseCases.allDomain
    

    private var _calendarUIState = mutableStateOf(CalendarUIState())
    val calendarUIState: State<CalendarUIState> = _calendarUIState

    init {
        onChangeQuery("")
    }

    fun switchVisibilityOfDialogue() {
        _calendarUIState.value =
            _calendarUIState.value.copy(isDomainDialogueVisible = !_calendarUIState.value.isDomainDialogueVisible)
    }

    fun fetchColors(context: Context, afterSuccess: (List<CalendarColor>)->Unit){
        viewModelScope.launch {
            afterSuccess(calendarRepository.fetchColors(context))
        }
    }

    fun refresh(context: Context) {
        viewModelScope.launch {
            _calendarUIState.value = _calendarUIState.value.copy(
                events = calendarScreenUseCases.fetchFilteredEventsOFLastWeek(context),
                savedEvents = calendarScreenUseCases.savedEntries.first()
            )
            getEmptySlots()
            fetchHistory()
            onChangeQuery(_calendarUIState.value.queryText)
        }
    }

    fun rejectEvent(context: Context) {
        if (_calendarUIState.value.selectedEvent == null) {
            Toast.makeText(context, "Select an event to reject", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch {
            calendarScreenUseCases.rejectEvent(_calendarUIState.value.selectedEvent!!)
        }
        selectEvent(null)
        refresh(context)
    }

    fun approveEvent(context: Context) {
        if (_calendarUIState.value.selectedEvent == null || _calendarUIState.value.selectedDomain == null) {
            Toast.makeText(context, "Select an event and a domain to approve", Toast.LENGTH_SHORT)
                .show()
            return
        }
        viewModelScope.launch {
            calendarScreenUseCases.approveEvent(
                _calendarUIState.value.selectedEvent!!,
                _calendarUIState.value.selectedDomain!!
            )
            selectEvent(null)
            selectDomain(null)
            refresh(context)
        }
    }

    fun selectDomain(domainEntity: DomainEntity?) {
        _calendarUIState.value = _calendarUIState.value.copy(selectedDomain = domainEntity)
    }

    fun selectEvent(calendarEvent: CalendarEvent?) {
        _calendarUIState.value = _calendarUIState.value.copy(selectedEvent = calendarEvent)
    }

    fun toggleFilter(boolean: Boolean) {
        _calendarUIState.value = _calendarUIState.value.copy(isFilterApplied = boolean)
    }

    fun validate(calendarEvent: CalendarEvent, calId: Long?): Boolean {
        if (calendarEvent.calendarId == calId && _calendarUIState.value.isFilterApplied) {
            return false
        }
        if (calendarEvent.status == CalendarEventStatus.REJECTED || calendarEvent.status == CalendarEventStatus.ADDED) {
            return false
        }
        val saved = _calendarUIState.value.savedEvents
        if (saved.find { it.id == calendarEvent.id && it.dtstart == calendarEvent.dtstart } != null) {
            return false
        }
        return true
    }

    fun getCalenderAtIndex(index: Int?): CalendarType? {
        if (index == null)
            return null
        if (index < _calendarsList.value.size) {
            return _calendarsList.value[index - 1]
        }
        return null
    }

    fun fetchCalenders(context: Context) {
        viewModelScope.launch {
            _calendarsList.value = calendarRepository.fetchCalendars(context)
        }
    }

    fun switchVisibilityOfSearchComposable() {
        _calendarUIState.value = _calendarUIState.value.copy(isSearchComposableVisible = !_calendarUIState.value.isSearchComposableVisible)
    }

    fun onClickSlot(slot: Pair<Long, Long>) {
        if (_calendarUIState.value.selectedEmptySlots.contains(slot)) {
            _calendarUIState.value =
                _calendarUIState.value.copy(selectedEmptySlots = _calendarUIState.value.selectedEmptySlots - slot)
        } else {
            _calendarUIState.value =
                _calendarUIState.value.copy(selectedEmptySlots = _calendarUIState.value.selectedEmptySlots + slot)
        }
    }

    fun getEmptySlots(){
        val events = _calendarUIState.value.events
        _calendarUIState.value = _calendarUIState.value.copy(allEmptySlots = CalendarTrackerUtil.fetchEmptyTimeSlots(events).sortedByDescending { it.first })
    }


    fun onSelectDistinctName(distinctNames: DistinctNames) {
        if (_calendarUIState.value.selectedName == distinctNames) {
            _calendarUIState.value = _calendarUIState.value.copy(selectedName = null)
        } else {
            _calendarUIState.value = _calendarUIState.value.copy(selectedName = distinctNames)
        }
    }

    fun onChangeQuery(query: String){
        _calendarUIState.value = _calendarUIState.value.copy(queryText = query)
        viewModelScope.launch {
            if (query.isEmpty()) {
                _calendarUIState.value =
                    _calendarUIState.value.copy(matchingNames = _calendarUIState.value.allDistinctNames.first())
            } else {
                _calendarUIState.value =
                    _calendarUIState.value.copy(matchingNames = _calendarUIState.value.allDistinctNames.first().filter {
                        it.name.contains(query, ignoreCase = true)
                    })
            }
            val histories = historyUseCases.getHistory().first()
            //Reduce size of matching to 6
            if (_calendarUIState.value.matchingNames.size > 6) {
                _calendarUIState.value = _calendarUIState.value.copy(
                    matchingNames = _calendarUIState.value.matchingNames.sortedWith (
                        compareByDescending { it: DistinctNames->
                            histories.count { historyEntity ->
                                historyEntity.name == it.name && historyEntity.domainName == it.domainName
                            }
                        }
                    ).subList(
                        0,
                        6
                    )
                )
            }
        }
    }

    fun onSaveInSearchScreen(context: Context) {
        viewModelScope.launch {

            val mergedSlots =
                CalendarTrackerUtil.mergeContiguousTimeSlots(_calendarUIState.value.selectedEmptySlots)
            if (_calendarUIState.value.selectedName == null && _calendarUIState.value.queryText.isEmpty()) {
                Toast.makeText(
                    context,
                    "Select a domain or search for a domain first",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }
            if (_calendarUIState.value.selectedName == null) {
                switchVisibilityOfDialogue()
                return@launch
            }
            val historyList: MutableList<HistoryEntity> = mutableListOf()
            mergedSlots.forEach {
                val selectedName = _calendarUIState.value.selectedName!!
                historyList.add(
                    HistoryEntity(
                        start = it.first,
                        end = it.second,
                        name = selectedName.name,
                        description = selectedName.description,
                        domainEntityId = selectedName.domainEntityId,
                        domainName = selectedName.domainName,
                        productivityColor = _calendarUIState.value.selectedProductivityColor.color,
                        hId =0
                    )
                )
            }
            _calendarUIState.value = _calendarUIState.value.copy(generatedHistoryToBeSaved = historyList)
            makeConfirmationVisible()

        }

    }

    fun saveAfterDialogueGetsClosed(context: Context){
        val mergedSlots =
            CalendarTrackerUtil.mergeContiguousTimeSlots(_calendarUIState.value.selectedEmptySlots)
        viewModelScope.launch {
            if (_calendarUIState.value.selectedDomain == null) {
                Toast.makeText(context, "Select a domain first", Toast.LENGTH_SHORT).show()
                return@launch
            }
            val historyList: MutableList<HistoryEntity> = mutableListOf()
            val selectedDomain = _calendarUIState.value.selectedDomain!!
            mergedSlots.forEach {
                historyList.add(
                    HistoryEntity(
                        start = it.first,
                        end = it.second,
                        name = _calendarUIState.value.queryText,
                        description = "",
                        domainEntityId = selectedDomain.id,
                        domainName = selectedDomain.name,
                        productivityColor = _calendarUIState.value.selectedProductivityColor.color,
                        hId = 0
                    )
                )
            }
            _calendarUIState.value = _calendarUIState.value.copy(generatedHistoryToBeSaved = historyList)
            makeConfirmationVisible()

        }

    }

    private fun makeConfirmationVisible(){
        _calendarUIState.value = _calendarUIState.value.copy(isConfirmModeActive = true)
    }

    fun onSelectConfirmButton(context: Context){
        viewModelScope.launch {
            _calendarUIState.value.generatedHistoryToBeSaved.forEach {
                historyUseCases.useTime(context,it)
            }
        resetAfterSaving(context)
        }

    }

    fun fetchHistory(){
        viewModelScope.launch {
            val history = historyRepository.getDistinctNames()
            _calendarUIState.value = _calendarUIState.value.copy(allDistinctNames = history)

        }
    }

    fun resetAfterSaving(context: Context){
        _calendarUIState.value = _calendarUIState.value.copy(
            isConfirmModeActive = false,
            isSearchComposableVisible = false,
            selectedEmptySlots = emptyList(),
            selectedDomain = null,
            selectedName = null,
            queryText = "",
            generatedHistoryToBeSaved = mutableListOf(),
            selectedProductivityColor = CalendarColor.default
        )
        _calendarUIState.value.selectedName?.let { onSelectDistinctName(it) }

        refresh(context)
    }

    fun onToggleVisiblilityOfProductivityBottomSheet(){
        _calendarUIState.value = _calendarUIState.value.copy(isProductivityBottomSheetVisible = !_calendarUIState.value.isProductivityBottomSheetVisible)
    }

    fun onSelectColor(color: CalendarColor){
        _calendarUIState.value = _calendarUIState.value.copy(selectedProductivityColor = color)
    }

    // Show sleep dialog with detected sleep slots
    fun showSleepDialog(context: Context) {
        viewModelScope.launch {
            val sleepSlots = usageRepository.getUsageDataOfApps()
            val events = calendarRepository.fetchEventsOFLastWeek(context)

            // Filter to keep only free slots
            val freeSlots = sleepSlots.filter { slot ->
                CalendarTrackerUtil.isTimeSlotFree(slot.first, slot.second, events)
            }

            if (freeSlots.isEmpty()) {
                Toast.makeText(context, "No sleep patterns detected", Toast.LENGTH_SHORT).show()
                return@launch
            }

            _calendarUIState.value = _calendarUIState.value.copy(
                isSleepDialogVisible = true,
                sleepSlots = freeSlots
            )

            Log.i("CalendarViewModel", "Sleep slots detected: ${freeSlots.size}")
        }
    }

    // Dismiss sleep dialog
    fun dismissSleepDialog() {
        _calendarUIState.value = _calendarUIState.value.copy(
            isSleepDialogVisible = false,
            sleepSlots = emptyList()
        )
    }

    // Save sleep data with selected domain
    fun saveSleepData(context: Context, domainId: Int, domainName: String, selectedSlots: List<Pair<Long, Long>>, color: CalendarColor) {
        viewModelScope.launch {
            if (selectedSlots.isEmpty()) {
                Toast.makeText(context, "No sleep slots selected", Toast.LENGTH_SHORT).show()
                return@launch
            }

            Log.i("CalendarViewModel", "Saving ${selectedSlots.size} sleep slots")

            selectedSlots.forEach { slot ->
                Log.i("CalendarViewModel", "Saving sleep: ${TimeUtils.convertMillisToString(slot.first)} to ${TimeUtils.convertMillisToString(slot.second)}")
                historyUseCases.useTime(
                    context,
                    HistoryEntity(
                        start = slot.first,
                        end = slot.second,
                        name = "Sleep",
                        description = "",
                        domainEntityId = domainId,
                        domainName = domainName,
                        productivityColor = color.color,
                        hId = 0
                    )
                )
            }

            // Dismiss dialog and refresh
            dismissSleepDialog()
            refresh(context)
            Toast.makeText(context, "Sleep data saved successfully", Toast.LENGTH_SHORT).show()
        }
    }

    // Legacy method - now just shows the dialog
    fun addSleep(context: Context) {
        showSleepDialog(context)
    }
}
