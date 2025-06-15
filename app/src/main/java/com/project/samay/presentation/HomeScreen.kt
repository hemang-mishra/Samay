package com.project.samay.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import org.koin.androidx.compose.koinViewModel

/**
 * Navigation items for the bottom navigation bar
 */
enum class NavItem(
    val label: String,
    val notSelectedIcon: ImageVector,
    val icon: ImageVector
) {
    CALENDAR(
        label = "Calendar",
        notSelectedIcon = Icons.Outlined.CalendarToday,
        icon = Icons.Default.CalendarToday
    ),
    APPS(
        label = "Apps",
        notSelectedIcon = Icons.Outlined.Apps,
        icon = Icons.Default.Apps
    ),
    DOMAINS(
        label = "Domains",
        notSelectedIcon = Icons.Outlined.Category,
        icon = Icons.Default.Category
    ),
    FOCUS(
        label = "Focus",
        notSelectedIcon = Icons.Outlined.Watch,
        icon = Icons.Default.Watch
    ),
    TASKS(
        label = "Tasks",
        notSelectedIcon = Icons.Outlined.Task,
        icon = Icons.Default.Task
    )
}

@Serializable
object NavHomeScreen

@Composable
fun HomeScreen(
    domainViewModel: DomainViewModel = koinViewModel<DomainViewModel>(),
    taskViewModel: TaskViewModel = koinViewModel<TaskViewModel>(),
    calendarViewModel: CalendarViewModel = koinViewModel<CalendarViewModel>(),
    monitorViewModel: MonitorViewModel = koinViewModel<MonitorViewModel>(),
    navController: NavHostController,
    service: StopwatchService
) {
    val pagerState = rememberPagerState(pageCount = { NavItem.entries.size })
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(navController)
        }
    ) {
        Scaffold(
            bottomBar = {
                HomeNavigationBar(
                    pagerState = pagerState,
                    onNavigationItemClick = { index ->
                        scope.launch {
                            pagerState.scrollToPage(index)
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.padding(innerPadding)
                ) { page ->
                    when (NavItem.entries[page]) {
                        NavItem.DOMAINS -> DomainScreen(
                            domainViewModel = domainViewModel,
                            navController = navController
                        )
                        NavItem.TASKS -> TasksScreen(
                            taskViewModel = taskViewModel,
                            navController = navController
                        )
                        NavItem.CALENDAR -> CalenderScreen(
                            calendarViewModel = calendarViewModel
                        )
                        NavItem.APPS -> MonitorScreen(
                            viewModel = monitorViewModel
                        )
                        NavItem.FOCUS -> FocusScreen(
                            stopwatchService = service
                        )
                    }
                }

                MenuButton(
                    onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun HomeNavigationBar(
    pagerState: androidx.compose.foundation.pager.PagerState,
    onNavigationItemClick: (Int) -> Unit
) {
    NavigationBar {
        NavItem.entries.forEachIndexed { index, navItem ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (pagerState.currentPage == index) {
                            navItem.icon
                        } else {
                            navItem.notSelectedIcon
                        },
                        contentDescription = navItem.label
                    )
                },
                label = { Text(navItem.label) },
                selected = pagerState.currentPage == index,
                onClick = { onNavigationItemClick(index) }
            )
        }
    }
}

@Composable
private fun MenuButton(
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier.padding(
            start = 4.dp,
            top = 20.dp
        ),
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Open navigation drawer"
        )
    }
}