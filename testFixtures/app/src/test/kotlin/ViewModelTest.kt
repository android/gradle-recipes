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
import org.junit.Test

/**
 * JUnit test class that uses `lib` fixtures
 * to run test cases.
 */
class ViewModelTest{

    @Test
    fun updateUserLogic(){
        val fixture = UserRepoFixture()
        val model = ViewModel(fixture.getRepository())
        val user = model.users[0].copy(status = "sick")
        model.updateStatus(user.id, user.status)

        fixture.inDataSet(user)
        fixture.assertEventIsUpdateUser(model.getEvents()[0])
    }
}
