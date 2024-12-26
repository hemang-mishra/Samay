package com.project.samay.presentation.history

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.samay.domain.model.DomainEntity
import com.project.samay.domain.model.HistoryEntity
import com.project.samay.presentation.components.BoldItalicText
import com.project.samay.presentation.domains.DomainViewModel
import com.project.samay.presentation.domains.NavAddDomainScreen
import com.project.samay.presentation.domains.NavUseDomainScreen
import com.project.samay.presentation.domains.toOneDecimalPlace

@Composable
fun MainHistoryScreen(viewModel: HistoryViewModel){

}

@Composable
private fun HistoryScreen(){

}


@Composable
fun HistoryItem(historyEntity: HistoryEntity, isSelected: Boolean, onClick: ()->Unit) {
    val timeSpent = (historyEntity.end- historyEntity.start)/60/1000

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
//                    Text(text = "${domain.presentPercentage.toOneDecimalPlace()}%")
                    BoldItalicText(
                        text = " of",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                HorizontalDivider(modifier = Modifier.width(36.dp))
//                Text(text = "${domain.expectedPercentage.toOneDecimalPlace()}%")
            }
        }
        if (isSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                BoldItalicText(
                    text = "Description: ",
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(text = historyEntity.description)
            }
            Row(modifier = Modifier.align(Alignment.End)) {

                IconButton(onClick = {

                }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
    HorizontalDivider()

}
