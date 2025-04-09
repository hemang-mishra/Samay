import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.project.samay.domain.model.CalendarColor
import com.project.samay.domain.util.Preferences.PRODUCTIVITY_LEVELS
import com.project.samay.util.ProductivityColors
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProductivityComposable(
    heading: String,
    selectedColor: CalendarColor? = null,
    onClick: (String, CalendarColor) -> Unit
) {
    val context = LocalContext.current
    var colorList by remember { mutableStateOf<List<CalendarColor>>(emptyList()) }

    LaunchedEffect(Unit) {
        ProductivityColors.readCalendarColors(context).collectLatest {
            colorList = it
        }
    }

    Column(modifier = Modifier
        .fillMaxWidth()) {

        Text(
            text = heading,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        PRODUCTIVITY_LEVELS.indices.forEach { index ->
            val levelName = PRODUCTIVITY_LEVELS[index]
            val calendarColor = colorList.getOrNull(index) ?: CalendarColor.default
            val isSelected = selectedColor?.key == index

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClick(levelName, calendarColor)
                    }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = Color(calendarColor.color),
                            shape = CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = levelName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider()
        }
    }
}
