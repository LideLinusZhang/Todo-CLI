package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.mordant.terminal.Terminal
import edu.uwaterloo.cs.todo.lib.getHashedPassword
import kotlinx.coroutines.runBlocking
import sync.CloudService

class SignUp(private val cloudService: CloudService?) : CliktCommand("Create an account for synchronization.") {
    private val terminal = Terminal()

    override fun run() {
        if (cloudService === null)
            throw PrintMessage(
                "Cannot connect to the Internet for sign up with synchronization service disabled.",
                error = true
            )

        val userName = prompt("Username")!!
        val password = prompt("Password", hideInput = true, requireConfirmation = true)!!

        val response = runBlocking { cloudService.signUp(userName, getHashedPassword(userName, password)) }

        if (!response.successful)
            throw PrintMessage("Registration of new account failed: ${response.errorMessage}", error = true)
        else terminal.println("Registration successful.")
    }
}