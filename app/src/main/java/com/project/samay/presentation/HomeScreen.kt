package com.project.samay.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.project.samay.domain.service.StopwatchService
import com.project.samay.presentation.calender.CalendarViewModel
import com.project.samay.presentation.calender.CalenderScreen
import com.project.samay.presentation.domains.DomainScreen
import com.project.samay.presentation.domains.DomainViewModel
import com.project.samay.presentation.focus.FocusScreen
import com.project.samay.presentation.monitor.MonitorScreen
import com.project.samay.presentation.monitor.MonitorViewModel
import com.project.samay.presentation.tasks.TaskViewModel
import com.project.samay.presentation.tasks.TasksScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

enum class NavItem(val label: String, val notSelectedIcon: ImageVector, val icon: ImageVector) {
    CALENDAR("Calendar", Icons.Outlined.CalendarToday, Icons.Default.CalendarToday),
    APPS("Apps", Icons.Outlined.Apps, Icons.Default.Apps),
    DOMAINS("Domains", Icons.Outlined.Category, Icons.Default.Category),
    FOCUS(
        "Focus",
        Icons.Outlined.Watch,
        Icons.Default.Watch,
    ),
    TASKS(
        "Tasks",
        Icons.Outlined.Task,
        Icons.Default.Task,
    )
}

@Serializable
object NavHomeScreen

@Composable
fun HomeScreen(
    domainViewModel: DomainViewModel,
    taskViewModel: TaskViewModel,
    calendarViewModel: CalendarViewModel,
    monitorViewModel: MonitorViewModel,
    navController: NavHostController,
    service: StopwatchService
) {
    val pagerState = rememberPagerState(pageCount = { NavItem.entries.size })
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        NavigationDrawerContent(navController)
    }) {
        Scaffold(bottomBar = {
            NavigationBar {
                NavItem.entries.forEachIndexed { index, navItem ->
                    NavigationBarItem(icon = {
                        Icon(
                            imageVector = if (pagerState.currentPage == index) navItem.icon else navItem.notSelectedIcon,
                            contentDescription = navItem.label
                        )
                    },
                        label = { Text(navItem.label) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.scrollToPage(index)
                            }
                        })
                }
            }
        }) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState, modifier = Modifier.padding(innerPadding)
                ) { page ->
                    when (NavItem.entries[page]) {
                        NavItem.DOMAINS -> DomainScreen(
                            domainViewModel,
                            navController = navController
                        )

                        NavItem.TASKS -> TasksScreen(taskViewModel, navController)
                        NavItem.CALENDAR -> CalenderScreen(calendarViewModel = calendarViewModel)
                        NavItem.APPS -> MonitorScreen(monitorViewModel)
                        NavItem.FOCUS -> FocusScreen(service)
                    }
                }

                IconButton(modifier = Modifier.padding(start = 4.dp, top = 20.dp),
                    onClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                }
            }
        }
    }
}

