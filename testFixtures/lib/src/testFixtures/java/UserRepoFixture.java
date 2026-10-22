/*
 * Copyright (C) 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.common.truth.Truth;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class UserRepoFixture {

    private Map<Integer, User> users;

    public UserRepoFixture() {
        users = new HashMap<>();
        users.put(1, new User(1, "Bob", "Wilson", "active"));
        users.put(2, new User(2, "John", "Johnson", "vacation"));
    }

    public UserRepository getRepository() {
        return new UserRepository(){
            private List<Integer> events = new ArrayList<>();
            public List<User> getUsers(){
                return new ArrayList<>(users.values());
            }
            public void updateUser(User user) {
                users.put(user.getId(), user);
                events.add(com.example.android.recipes.fixtureLib.R.string.userUpdated);
            }
            public List<Integer> getUserEvents(){
                return new ArrayList<>(events);
            }
        };
    }

    public List<User> getUsers(){
        return new ArrayList<>(users.values());
    }
    public void inDataSet(User user) {
        Truth.assertThat(users.get(user.getId())).isEqualTo(user);
    }

    public void assertEventIsUpdateUser(int event) {
        Truth.assertThat(event)
            .isEqualTo(com.example.android.recipes.fixtureLib.R.string.userUpdated);
    }
}
