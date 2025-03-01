import com.google.common.truth.Truth

import com.example.android.recipes.fixtureLib.R as AppR

class KotlinUserRepoFixture {
    private val users: MutableMap<Int, User> =
        HashMap()

    init {
        users[1] = User(1, "Bob", "Wilson", "active")
        users[2] = User(2, "John", "Johnson", "vacation")
    }

    val repository: UserRepository
        get() = object : UserRepository {
            private val events: MutableList<Int> =
                ArrayList()

            override fun getUsers(): List<User> {
                return ArrayList(users.values)
            }

            override fun updateUser(user: User) {
                users[user.id] = user
                events.add(AppR.string.userUpdated)
            }

            override fun getUserEvents(): List<Int> {
                return ArrayList(events)
            }
        }

    fun getUsers(): List<User> {
        return ArrayList(users.values)
    }

    fun inDataSet(user: User) {
        Truth.assertThat(users[user.id]).isEqualTo(user)
    }

    fun assertEventIsUpdateUser(event: Int) {
        Truth.assertThat(event)
            .isEqualTo(AppR.string.userUpdated)
    }
}
