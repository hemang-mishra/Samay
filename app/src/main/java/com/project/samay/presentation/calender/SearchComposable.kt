package com.project.samay.presentation.calender

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.samay.domain.model.DistinctNames
import com.project.samay.domain.model.HistoryEntity
import com.project.samay.presentation.components.BoldItalicText
import com.project.samay.presentation.components.PrimaryAppButton
import com.project.samay.presentation.history.HistoryItem
import com.project.samay.ui.theme.SamayTheme

@Composable
fun SearchComposable(
    query: String,
    matchingNames: List<DistinctNames>,
    selectedName: DistinctNames?,
    isConfirmPromptVisible: Boolean = false,
    generatedHistory: List<HistoryEntity>,
    onSelectName: (DistinctNames) -> Unit,
    onChangeQuery: (String) -> Unit,
    onConfirm: ()->Unit,
    onClickSave: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        SearchBar(query) {
            onChangeQuery(it)
        }
        Spacer(Modifier.height(16.dp))
        BoldItalicText(text = if(isConfirmPromptVisible) "The slots are:" else "Matching Names", fontSize = 14)
        Spacer(Modifier.height(16.dp))
        AnimatedContent(isConfirmPromptVisible) {
            if(it){
                Column {
                    generatedHistory.forEach {
                        HistoryItem(
                            it,
                            false,
                            {
                                // deleteHistory(it)
                            },
                            {
                                // selectHistory(it)
                            }
                        )
                    }
                }
            }else {
                Column {
                    matchingNames.forEach {
                        SearchItem(distinctNames = it, isSelected = it == selectedName, onClick = {
                            onSelectName(it)
                        })
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        PrimaryAppButton(
            if(!isConfirmPromptVisible) onClickSave else onConfirm,
            Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally), if(!isConfirmPromptVisible)"Save" else "Confirm", Icons.Default.Save
        )
    }
}

@Composable
fun SearchBar(text: String, onChangeQuery: (String) -> Unit) {
    OutlinedTextField(text, onChangeQuery, label = { Text("Search") }, leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = null)
    },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SearchItem(distinctNames: DistinctNames, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor =
        if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    Column(
        modifier = Modifier
            .background(backgroundColor)
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
                tint = Color(distinctNames.productivityColor),
                modifier = Modifier
                    .align(Alignment.Top)
                    .width(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(0.4f)
            ) {
                BoldItalicText(text = distinctNames.name, fontSize = 24)
                Text(text = distinctNames.description)
            }

        }

    }
    HorizontalDivider()

}


@Preview
@Composable
fun SearchComposablePreview() {
    SamayTheme {
        Surface {
//            SearchComposable(
//                "Hemang",
//                listOf(
//                    DistinctNames("name", "description", 1, ProfileColors.LIME.hex, "name"),
//                    DistinctNames("name", "description", 1, ProfileColors.LIME.hex, "name"),
//                    DistinctNames("name", "description", 1, ProfileColors.LIME.hex, "name")
//                ),
//                DistinctNames("name", "description", 1, ProfileColors.LIME.hex, "name"),
////                null,
//                false,
//                emptyList(),
//                onSelectName = {},
//                onConfirm = {},
//                onChangeQuery = {},
//            ) { }
        }
    }
}