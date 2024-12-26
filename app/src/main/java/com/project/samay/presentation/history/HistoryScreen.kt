package com.project.samay.presentation.history

import android.provider.ContactsContract.Profile
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.samay.domain.model.DomainEntity
import com.project.samay.domain.model.HistoryEntity
import com.project.samay.presentation.components.BoldItalicText
import com.project.samay.presentation.components.TopAppBarGoal
import com.project.samay.presentation.domains.DomainItem
import com.project.samay.presentation.domains.DomainViewModel
import com.project.samay.presentation.domains.NavAddDomainScreen
import com.project.samay.presentation.domains.NavUseDomainScreen
import com.project.samay.presentation.domains.toOneDecimalPlace
import com.project.samay.util.ProfileColors
import com.project.samay.util.calculations.TimeUtils

@Composable
fun MainHistoryScreen(historyViewModel: HistoryViewModel){
    val uiState by historyViewModel.historyUiState.collectAsState(HistoryScreenUIState())
    val history by uiState.historyItems.collectAsState(initial = emptyList())
    HistoryScreen(history, uiState.selectedHistory,{
        historyViewModel.deleteHistory(it)
    }) {
        historyViewModel.selectHistory(it)
    }
}

@Composable
private fun HistoryScreen(history: List<HistoryEntity>, selectedHistory: HistoryEntity?, deleteHistory: (HistoryEntity) -> Unit, onSelectHistory: (HistoryEntity)->Unit){
    Scaffold { it ->
        Column(
            modifier = Modifier.padding(it)
        ) {
            TopAppBarGoal("History")
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(history) { _, historyEntity ->
                    HistoryItem(historyEntity,historyEntity == selectedHistory, {
                        deleteHistory(historyEntity)
                    }) {
                        onSelectHistory(historyEntity)
                    }
                }
                item {
                    Spacer(Modifier.height(100.dp))
                }
            }
        }
    }
}


@Composable
fun HistoryItem(historyEntity: HistoryEntity, isSelected: Boolean, deleteHistory:(HistoryEntity)->Unit, onClick: (HistoryEntity)->Unit) {
    val timeSpent = (historyEntity.end- historyEntity.start)/60/1000
    val startTime = TimeUtils.convertMillisToString(historyEntity.start)
    val relativeTime = TimeUtils.getRelativeTimeDescription(historyEntity.start)

    Column(
        modifier = Modifier
            .animateContentSize()
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .clickable { onClick(historyEntity) }
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
        ) {
            Icon(
                imageVector = Icons.Default.Category, contentDescription = null,
                tint = Color(historyEntity.domainColor),
                modifier = Modifier
                    .align(Alignment.Top)
                    .width(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(0.4f)
            ) {
                BoldItalicText(text = historyEntity.name, fontSize = 24)
                Text(text = historyEntity.domainName)
            }
            Column(
                modifier = Modifier
                    .weight(0.2f)
                    .padding(4.dp)
            ) {
                Row(modifier = Modifier) {
                    Text(text = (timeSpent).toString())
                    BoldItalicText(
                        text = " min",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                BoldItalicText(text = "spent")
            }
            Column(modifier = Modifier) {
                Row {
                    Text(
                        text = startTime,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                HorizontalDivider(modifier = Modifier.width(36.dp))
                BoldItalicText(text = relativeTime)
            }
        }
        if (isSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                BoldItalicText(
                    text = "Description: ",
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(text = historyEntity.description)
            }
            Row(modifier = Modifier.align(Alignment.End)) {
                IconButton(onClick = {
                    deleteHistory(historyEntity)
                }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
    HorizontalDivider()

}


@Preview
@Composable
fun PreviewHistoryScreen(){
    val history = listOf(
        HistoryEntity(
            1,
            "Work",
            "Work",
            TimeUtils.addMinutesToMillis(timeInMinutes = -180),
            TimeUtils.addMinutesToMillis(timeInMinutes = -120),
            1,
            ProfileColors.LIME.hex,
            "Work",
        ))
    HistoryScreen(history,history.get(0),{} ) { }

}