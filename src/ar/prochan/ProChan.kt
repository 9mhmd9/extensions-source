package eu.kanade.tachiyomi.extension.ar.prochan

import eu.kanade.tachiyomi.multisrc.madara.Madara
import java.text.SimpleDateFormat
import java.util.Locale

class ProChan : Madara(
    "Pro Chan",
    "https://prochan.net",
    "ar",
    SimpleDateFormat("MMMM d, yyyy", Locale("ar"))
)
