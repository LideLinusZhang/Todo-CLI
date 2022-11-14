package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.groups.cooccurring
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.mordant.terminal.Terminal
import edu.uwaterloo.cs.todo.lib.getHashedPassword
import kotlinx.coroutines.runBlocking
import sync.CloudService

class SignUp(private val cloudService: CloudService?) : CliktCommand("Create an account for synchronization.") {
    private val userCredentialOptions by UserCredentialOptions().cooccurring()
    private val terminal = Terminal()

    private class UserCredentialOptions : OptionGroup() {
        val userName by option("--username", hidden = true).required()
        val password by option("--password", hidden = true).required()
    }

    override fun run() {
        if (cloudService === null)
            throw PrintMessage(
                "Cannot connect to the Internet for sign up with synchronization service disabled.",
                error = true
            )

        val userName: String
        val password: String
        if (userCredentialOptions === null) {
            userName = prompt("Username")!!
            password = prompt("Password", hideInput = true, requireConfirmation = true)!!
        } else {
            userName = userCredentialOptions!!.userName
            password = userCredentialOptions!!.password
        }

        val response = runBlocking { cloudService.signUp(userName, getHashedPassword(userName, password)) }

        if (!response.successful)
            throw PrintMessage("Registration of new account failed: ${response.errorMessage}", error = true)
        else terminal.println("Registration successful.")
    }
}