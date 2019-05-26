import java.io.File
import java.io.FileInputStream
import java.util.*

interface IComponent<T> {
    fun decorate(): T
}

class Component<T> constructor(var t: T) : IComponent<T> {
    override fun decorate() = t
}


@Suppress("UNCHECKED_CAST")
sealed class Decorator<T>(var component: IComponent<T>) : IComponent<T> {

    class Wordify<T>(
        component: IComponent<T>, val lang: String = "en", val delimiter: String = "", val decorDelim: String = ","
    ) : Decorator<T>(component) {
        override fun decorate(): T {
            val res = component.decorate()
            return when (res) {
                is String -> {
                    val p = Properties()
                    var fis: FileInputStream? = null
                    try {
                        fis = FileInputStream("number-$lang.properties")
                        p.load(fis)
                    } catch (e: Exception) {
                        if (File("number-en.properties").exists()) {
                            fis = FileInputStream("number-en.properties")
                            println("Loading english as the properties for $lang is not found")
                            p.load(fis)
                        }
                    } finally {
                        fis?.close()
                    }
                    val temp = (res as String)
                    if (!temp.isEmpty()) {
                        temp.split(delimiter).reduce { acc, s ->
                            val word = p.getProperty(s)
                            if (!s.isEmpty() && word != null) {
                                if (!acc.isEmpty())
                                    "$acc$decorDelim$word"
                                else word
                            } else acc
                        } as T
                    } else res
                }
                else -> res
            }
        }
    }

    class Exclaim<T>(component: IComponent<T>) : Decorator<T>(component) {
        override fun decorate(): T {
            val res = component.decorate()
            return when (res) {
                is String ->
                    "$res!" as T
                else -> res
            }
        }
    }


    class Hyphenate<T>(component: IComponent<T>, var delimiter: String = "", var decorDelim: String = "-") :
        Decorator<T>(component) {
        override fun decorate(): T {
            val res = component.decorate()
            return when (res) {
                is String ->
                    (res as String).split(delimiter).reduce { acc, s ->
                        if (!s.isEmpty()) "$acc$decorDelim$s" else acc
                    } as T
                is Int -> -res as T
                else -> res
            }

        }
    }
}


fun main() {
    val component = Component(12345.toString())
    val wordify = Decorator.Wordify(component)
    val hyphenize = Decorator.Hyphenate(wordify, ",")
    val exclaim = Decorator.Exclaim(hyphenize)

    println(Decorator.Wordify(Component(12345.toString())).decorate())
    println(exclaim.decorate())
}