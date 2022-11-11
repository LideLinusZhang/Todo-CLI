package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.UsageError
import edu.uwaterloo.cs.todo.lib.getHashedPassword
import kotlinx.coroutines.runBlocking
import sync.SyncService

class SignUp(private val syncService: SyncService?) : CliktCommand("Create an account for synchronization.") {
    override fun run() {
        if(syncService === null)
            throw UsageError("Cannot connect to the Internet for sign up with synchronization service disabled.")

        val userName = prompt("Username")!!
        val password = prompt("Password", hideInput = true, requireConfirmation = true)!!

        val response = runBlocking { syncService.signUp(userName, getHashedPassword(userName, password)) }

        if (!response.successful)
            throw UsageError("Registration of new account failed: ${response.errorMessage}")
    }
}