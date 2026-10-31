package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.utils.swipeDirectionArrow
import io.github.toldry.glyphsketch.Charset

/** The list search field and candidate rows shared by the sketch lists. */
@Composable
internal fun SearchField(
    text: String,
    hint: String,
    modifier: Modifier = Modifier,
    cursor: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .heightIn(min = 40.dp)
                .padding(horizontal = 12.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        SearchFieldText(text, hint, cursor)
    }
}

@Composable
private fun SearchFieldText(
    text: String,
    hint: String,
    cursor: Boolean,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (text.isNotEmpty()) {
            Text(text = text, fontSize = 15.sp, maxLines = 1, color = MaterialTheme.colorScheme.onSurface)
        }
        if (cursor) {
            Box(
                modifier =
                    Modifier
                        .width(2.dp)
                        .height(18.dp)
                        .background(MaterialTheme.colorScheme.primary),
            )
        }
        if (text.isEmpty()) {
            Text(
                text = hint,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 2.dp),
            )
        }
    }
}

/** One row of a list: number, character, name, code point and, if it's on a key, where. */
@Composable
internal fun CandidateRow(
    row: SketchRow,
    charset: Charset,
    onPick: (text: String) -> Unit,
) {
    val info = charset.characters.getValue(row.codePoint)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .clickable { onPick(info.text) }
                .padding(start = 8.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
    ) {
        Text(
            text = row.number.toString(),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.End,
            modifier = Modifier.width(30.dp),
        )
        Text(
            text = info.displayText,
            fontSize = 28.sp,
            lineHeight = 30.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(48.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = info.name,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = codePointLabel(row.codePoint),
                fontSize = 10.5.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        row.slot?.let { (key, direction) ->
            Text(
                text = stringResource(R.string.sketch_key_position, key + 1, swipeDirectionArrow(direction)),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
}

private fun codePointLabel(codePoint: Int) = "U+%04X".format(codePoint)
