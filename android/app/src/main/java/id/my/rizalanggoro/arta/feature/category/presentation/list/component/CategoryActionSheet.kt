package id.my.rizalanggoro.arta.feature.category.presentation.list.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.my.rizalanggoro.arta.core.constant.toCategoryType
import id.my.rizalanggoro.arta.openapi.models.DomainCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryActionSheet(
    category: DomainCategory,
    onClickEdit: () -> Unit = {},
    onClickDelete: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(category.name, style = MaterialTheme.typography.titleLarge)
                Text(
                    category.type.toCategoryType(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Column {
                ListItem(
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    leadingContent = {
                        Icon(
                            Icons.Rounded.Edit,
                            null
                        )
                    },
                    headlineContent = {
                        Text("Ubah")
                    },
                    trailingContent = {
                        Icon(
                            Icons.Rounded.ChevronRight,
                            null
                        )
                    },
                    modifier = Modifier.clickable {
                        onClickEdit()
                    }
                )

                ListItem(
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    leadingContent = {
                        Icon(
                            Icons.Rounded.Delete,
                            null
                        )
                    },
                    headlineContent = {
                        Text("Hapus")
                    },
                    trailingContent = {
                        Icon(
                            Icons.Rounded.ChevronRight,
                            null
                        )
                    },
                    modifier = Modifier.clickable {
                        onClickDelete()
                    }
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun Preview() {
    CategoryActionSheet(
        category = DomainCategory(
            id = 1,
            name = "Makanan",
            type = "expense",
            userId = 1,
            createdAt = "",
            updatedAt = "",
        )
    )
}