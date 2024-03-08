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

/**
 * ViewModel gets list of users from `UserRepository` located in `lib`.
 * It updates status for a user and can get events from repository
 */
class ViewModel(val repository: UserRepository) {

  val users: List<User>

  init {
    users = repository.getUsers()
  }

  fun updateStatus(id: Int, status: String) {
    val user = users.firstOrNull { it.id == id } ?: return
    repository.updateUser(user.copy(status = status))
  }

  /**
   * Gets list of events that happened with repository. For simplicity
   * each updateUser call will generate `update user` event.
   * Event is set as a resource id.
   */
  fun getEvents(): List<Int> {
    return repository.getUserEvents();
  }
}