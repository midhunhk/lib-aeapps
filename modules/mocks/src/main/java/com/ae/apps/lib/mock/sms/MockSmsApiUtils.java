/*
 * Copyright (c) 2018 Midhun Harikumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ae.apps.lib.mock.sms;

import com.ae.apps.lib.common.models.MessageInfo;

import java.util.ArrayList;
import java.util.List;

public class MockSmsApiUtils {

    public static MessageInfo getMessageInfo(){
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setId("11");
        messageInfo.setPerson("22");
        messageInfo.setDate(100);
        messageInfo.setProtocol(null);
        messageInfo.setType("sent");
        return messageInfo;
    }

    public static List<MessageInfo> getMessageInfoList(final int size){
        List<MessageInfo> messages = new ArrayList<>();
        MessageInfo messageInfo;
        for(int i = 0; i < size; i++){
            messageInfo = getMessageInfo();
            messageInfo.setId(String.valueOf(i));
            messages.add(messageInfo);
        }
        return messages;
    }
}
