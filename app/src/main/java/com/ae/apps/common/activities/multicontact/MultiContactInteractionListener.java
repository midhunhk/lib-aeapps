package com.ae.apps.common.activities.multicontact;

import com.ae.apps.common.vo.ContactVo;

import java.util.List;

/**
 * Interaction listener for MultiContactPicker
 */
interface MultiContactInteractionListener {

    /**
     * the contacts list which is to be displayed in the picker
     *
     * @return contacts list
     */
    List<ContactVo> contactsList();

    /**
     * Invoked when a contact is selected
     *
     * @param contactId contactId
     */
    void onContactSelected(String contactId);

    /**
     * Invoked when a contact is unselected
     *
     * @param contactId contactId
     */
    void onContactUnselected(String contactId);
}
