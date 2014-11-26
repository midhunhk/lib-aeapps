package com.ae.apps.common.mock;

import java.util.ArrayList;
import java.util.Random;

import com.ae.apps.aeappslibrary.R;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.common.vo.PhoneNumberVo;

public class MockContactDataUtils {

	/**
	 * Mock names, TODO read from resources
	 */
	private static String mockNamesEN[] = { "Elliot Hobbs", "Aiden Perry",
			"Daisy Forster", "Luis Gibson", "Martin J. Fox", "Catherine"};

	/**
	 * mock profile images
	 */
	private static int mockProfileImages[] = { R.drawable.profile_icon_1,
			R.drawable.profile_icon_2, R.drawable.profile_icon_3,
			R.drawable.profile_icon_4, R.drawable.profile_icon_5,
			R.drawable.profile_icon_6};

	/**
	 * Returns a mock contact
	 * 
	 * @param resource
	 * @return
	 */
	public static ContactVo getMockContact() {
		ContactVo contactVo = new ContactVo();

		// Create the phone number list and add one
		ArrayList<PhoneNumberVo> phoneNumbersList = new ArrayList<PhoneNumberVo>();
		PhoneNumberVo numberVo = new PhoneNumberVo();
		numberVo.setPhoneNumber("87 7781 6267");
		phoneNumbersList.add(numberVo);
		
		numberVo = new PhoneNumberVo();
		numberVo.setPhoneNumber("86 2343 6789");
		phoneNumbersList.add(numberVo);

		Random random = new Random();
		int randomVal = random.nextInt(mockNamesEN.length);
		String randomName = mockNamesEN[randomVal];

		// set mock details for this contact
		contactVo.setMockUser(true);
		contactVo.setMockProfileImageResource(mockProfileImages[randomVal]);
		
		contactVo.setName(randomName);
		contactVo.setId(String.valueOf(randomVal));
		contactVo.setTimesContacted(String.valueOf(randomVal));
		contactVo.setHasPhoneNumber(true);
		contactVo.setPhoneNumbersList(phoneNumbersList);

		return contactVo;
	}

}
