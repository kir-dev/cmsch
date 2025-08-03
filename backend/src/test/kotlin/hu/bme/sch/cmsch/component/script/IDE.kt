package hu.bme.sch.cmsch.component.script

import hu.bme.sch.cmsch.repository.UserRepository

/**
 * This function can help you write scripts with you IDE's code assist tools.
 * Copy the contents to a script on the site and enjoy.
 */
fun scriptWriter(
    context: hu.bme.sch.cmsch.component.script.sandbox.ScriptingContext,
    csv: hu.bme.sch.cmsch.component.script.sandbox.ScriptingCsvUtil,
    json: hu.bme.sch.cmsch.component.script.sandbox.ScriptingJsonUtil,
) {
    // COPY BELOW
    context.warn("")

    context.modifyingDb.repository(UserRepository::class).findAll().forEach {

    }

    context.readOnlyDb.repository(UserRepository::class).findAll().forEach { user ->

    }

}
