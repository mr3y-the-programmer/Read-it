import com.secret.readit.model.Article
import com.secret.readit.model.Category
import com.secret.readit.model.Comment
import com.secret.readit.model.Content
import com.secret.readit.model.Element
import com.secret.readit.model.Markup
import com.secret.readit.model.MarkupType
import com.secret.readit.model.Publisher

object TestData {
    val markupQuote = Markup(MarkupType.QUOTE, 0, 16)
    val markupText = markupQuote.copy(MarkupType.TEXT)
    val markupCode = markupQuote.copy(MarkupType.CODE)
    val markupBulletPoint = markupQuote.copy(MarkupType.BulletPoints)

    val textElement1 = Element("> This is a Quote", markupQuote, emptyList())
    val element2 = textElement1.copy("`This is Code block`", markupCode, emptyList())
    val element3 = textElement1.copy("* This Bullet Point around `code block`", markupBulletPoint, listOf(element2))
    val element4 = textElement1.copy("This is plain text", markupText, emptyList())
    val imageElement1 = Element("Not real image Uri")

    val elements1 = listOf(textElement1, element2, element3, element4, imageElement1)

    val content1 = Content(elements1)

    val publisher1 = Publisher("1pub", "fake1", "fake1@gamil.com", memberSince = 1280282737737)
    val publisher2 = publisher1.copy(id = "2pub", name = "fake2", emailAddress = "fake2@gmail.com")

    val comment0 = Comment("6", publisher2, "I've replied to you", 787542322223, emptyList())
    val comment1 = Comment("1", publisher1, "That's Awesome", 1968764334, emptyList())
    val comment2 = Comment("2", publisher1, "Fantastic", 788225123294, listOf(comment0))

    val comments1 = listOf(comment0, comment1, comment2)

    val category1 = Category("categ1", "Programming", 0xFF00F3)
    val category2 = Category("categ2", "Software Engineering", 0x00FFD4)

    val article1 = Article(
        "123", "article1", content1, publisher1, 2,
        1214343259253, comments1, category = listOf(category1, category2)
    )
}
