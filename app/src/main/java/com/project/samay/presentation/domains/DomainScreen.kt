package com.project.samay.presentation.domains

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.samay.domain.model.DomainEntity
import com.project.samay.presentation.components.BoldItalicText
import com.project.samay.presentation.components.TopAppBarGoal
import java.util.Locale


@Composable
fun DomainScreen(domainViewModel: DomainViewModel, navController: NavController) {
    val domains by domainViewModel.allDomains.collectAsState(initial = emptyList())
    val uiState by domainViewModel.uiStateValue
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBarGoal()
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(domains) { _, it ->
                    DomainItem(domain = it, isSelected = uiState.selectedDomain == it, viewModel = domainViewModel, navController = navController){
                        domainViewModel.selectDomain(it)
                    }
                }
                item{
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }
        FloatingActionButton(
            onClick = {
                navController.navigate(NavAddDomainScreen(false))
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add, contentDescription = null,
            )
        }
    }
}

@Composable
fun DomainItem(domain: DomainEntity, isSelected: Boolean, viewModel: DomainViewModel, navController: NavController, onClick: ()->Unit) {
    Column(
        modifier = Modifier
            .animateContentSize()
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .clickable { onClick() }
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
        ) {
            Icon(
                imageVector = Icons.Default.Category, contentDescription = null,
                tint = Color(domain.color),
                modifier = Modifier
                    .align(Alignment.Top)
                    .width(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(0.4f)
            ) {
                BoldItalicText(text = domain.name, fontSize = 24)
                Text(text = domain.description)
            }
            Column(
                modifier = Modifier
                    .weight(0.2f)
                    .padding(4.dp)
            ) {
                Row(modifier = Modifier) {
                    Text(text = (domain.timeSpent/60.0f).toOneDecimalPlace().toString())
                    BoldItalicText(
                        text = " hrs",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                BoldItalicText(text = "spent")
            }
            Column(modifier = Modifier) {
                Row {
                    Text(text = "${domain.presentPercentage.toOneDecimalPlace()}%")
                    BoldItalicText(
                        text = " of",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                HorizontalDivider(modifier = Modifier.width(36.dp))
                Text(text = "${domain.expectedPercentage.toOneDecimalPlace()}%")
            }
        }
        if (isSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                BoldItalicText(
                    text = "Montly target: ",
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(text = domain.monthlyTarget)
            }
            Row(modifier = Modifier.align(Alignment.End)) {
                IconButton(onClick = {
                    navController.navigate(NavUseDomainScreen)
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
                IconButton(onClick = {
                    navController.navigate(NavAddDomainScreen(true))
                }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                }
                IconButton(onClick = {
                    viewModel.deleteDomain(domain)
                }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
    HorizontalDivider()

}


fun Float.toOneDecimalPlace(): Float{
    val format = String.format(Locale.ROOT, "%.1f", this)
    return format.toFloat()
}

