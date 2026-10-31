package net.dungeonhub.model

import net.dungeonhub.model.ticket_panel.TicketPanelUpdateModel
import net.dungeonhub.service.MoshiService
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TicketPanelUpdateModelTest {
    @Test
    fun testCorrectSettingOfTicketLimits() {
        val updateModel = TicketPanelUpdateModel(
            null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, 1, 2, null, null, null, null, null, null, null
        )

        assertEquals(1, updateModel.openTicketLimit)
        assertEquals(false, updateModel.resetOpenTicketLimit)
        assertEquals(2, updateModel.closedTicketLimit)
        assertEquals(false, updateModel.resetClosedTicketLimit)

        updateModel.openTicketLimit = null
        updateModel.closedTicketLimit = 3

        assertEquals(null, updateModel.openTicketLimit)
        assertEquals(true, updateModel.resetOpenTicketLimit)
        assertEquals(3, updateModel.closedTicketLimit)
        assertEquals(false, updateModel.resetClosedTicketLimit)

        val newObject = MoshiService.moshi.adapter(TicketPanelUpdateModel::class.java).fromJson(updateModel.toJson())!!

        assertEquals(null, newObject.openTicketLimit)
        assertEquals(true, newObject.resetOpenTicketLimit)
        assertEquals(3, newObject.closedTicketLimit)
        assertEquals(false, newObject.resetClosedTicketLimit)
    }
}
