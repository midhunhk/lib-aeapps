package com.ae.apps.common.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.content.res.Resources;

import com.ae.apps.aeappslibrary.R;
import com.ae.apps.common.vo.ContactMessageVo;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.common.vo.PhoneNumberVo;

public class MockContactDataUtils {

	private static final String	LOCALE_FR			= "fr";
	private static final String	LOCALE_ES			= "es";

	/**
	 * Mock names, TODO read from resources
	 */
	private static String		mockNamesEN[]		= { "James Elliot", "Aiden Perry", "Daisy Forster", "Matt Gibson",
			"Martin J. Fox", "Catherine", "Scott Burns", "Jesse Whitehurst", "Diana Brown" };
	private static String		mockNamesES[]		= { "Bicor Adomo Abrego", "Fortuna Granado Fonseca",
			"Germana Ruvalcaba", "Sotero Jimnez Razo", "Olimpia Campos Curiel", "Folco Vega Girn",
			"Aidee Padrn Cazares"					};
	private static String		mockNamesFR[]		= { "Lyle Coulombe", "Saber Rivire", "Algernon Monjeau",
			"Emmeline Lamy", "Sylvie Mouet", "Carolos Bourgeau", "Ccile Fresne", "Loring Deslauriers" };

	/**
	 * mock profile images
	 */
	private static int			mockProfileImages[]	= { R.drawable.profile_icon_1, R.drawable.profile_icon_2,
			R.drawable.profile_icon_3, R.drawable.profile_icon_4, R.drawable.profile_icon_5, R.drawable.profile_icon_6,
			R.drawable.profile_icon_1, R.drawable.profile_icon_5, R.drawable.profile_icon_3};

	/**
	 * Returns a mock contact
	 * 
	 * @return ContactVo
	 */
	public static ContactVo getMockContact() {
		ContactVo contactVo = new ContactVo();

		// Create the phone number list and add one
		List<PhoneNumberVo> phoneNumbersList = new ArrayList<PhoneNumberVo>();
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

	/**
	 * A mock implementation for giving mock results. Used mainly for screenshots
	 *
	 * @param resource
	 * @return
	 */
	public static List<ContactMessageVo> getMockContactMessageList(Resources resource) {
		Random random = new Random();
		int startSeed = 180;
		int prevRandom = 0;
		int currRandom = 0;
		ContactVo contactVo;
		ContactMessageVo messageVo;
		List<ContactMessageVo> mockedList = new ArrayList<>();

		// Supply different set of mock names based on current locale, default is EN
		String[] mockNames;
		String locale = Locale.getDefault().getLanguage();
		if (LOCALE_ES.equalsIgnoreCase(locale)) {
			mockNames = mockNamesES;
		} else if (LOCALE_FR.equalsIgnoreCase(locale)) {
			mockNames = mockNamesFR;
		} else {
			mockNames = mockNamesEN;
		}

		// Some important calculations are about to happen
		for (String name : mockNames) {
			contactVo = new ContactVo();
			contactVo.setName(name);
			messageVo = new ContactMessageVo();
			messageVo.setContactVo(contactVo);
			if (prevRandom == 0) {
				currRandom = startSeed;
			} else {
				currRandom = random.nextInt(startSeed);
			}
			if (currRandom > prevRandom) {
				currRandom = (int) (currRandom * 0.75);
			}
			messageVo.setMessageCount(currRandom);
			prevRandom = currRandom;
			startSeed -= 2;
			mockedList.add(messageVo);
		}
		return mockedList;
	}

}
