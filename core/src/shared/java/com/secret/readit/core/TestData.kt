import com.secret.readit.core.uimodels.UiArticle
import com.secret.readit.core.uimodels.UiComment
import com.secret.readit.core.uimodels.UiPublisher
import com.secret.readit.model.*

object TestData {
    val markupQuote = Markup(MarkupType.QUOTE, 0, 20)
    val markupText = Markup(MarkupType.TEXT, 0, 43)
    val markupCode = Markup(MarkupType.CODE, 0, 90)
    val markupBulletPoint = Markup(MarkupType.BulletPoints, 0, 41)

    val oneLineQuoteElement = Element("> This is a Quote  >", markupQuote)
    val reverseOneLineQuoteElement = oneLineQuoteElement.copy(text = oneLineQuoteElement.text?.removeSurrounding(">"))
    val multipleLineQuoteElement = oneLineQuoteElement.copy(
        text = ">      This is twwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwooooooooooooooooooooooo" +
            " Liiinnnnneeeeeeeeeeeeeee Quoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooote>"
    )

    val codeBlockElement = Element(text = "` THis is code in Kotllllllllllllllllllin And These are some codes on Javaaaaaaaaaaaaaaaa`", markup = markupCode)
    val reverseCodeBlockElement = codeBlockElement.copy(text = codeBlockElement.text?.removeSurrounding("`"))

    val bulletPointElement = Element("~ This is One Bullet Point              ~", markupBulletPoint)
    val reverseBulletPointElement = bulletPointElement.copy(text = bulletPointElement.text?.removeSurrounding("~"))
    val multipleBulletPointElement = bulletPointElement.copy(text = "~                               THis is Multiple                  Bullet                   Point        ~")

    val plaintTextElement = Element(text = "This is just a simple text, No more than it", markup = markupText)

    val elements1 = listOf(oneLineQuoteElement, codeBlockElement, bulletPointElement, plaintTextElement)
    val reverseElements1 = listOf(reverseOneLineQuoteElement, reverseCodeBlockElement, reverseBulletPointElement, plaintTextElement)
    val elements2 = listOf(plaintTextElement, plaintTextElement, plaintTextElement, bulletPointElement, plaintTextElement, codeBlockElement)
    val reverseElements2 = listOf(plaintTextElement, plaintTextElement, plaintTextElement, reverseBulletPointElement, plaintTextElement, reverseCodeBlockElement)

    val content1 = Content(elements1)
    val reverseContent1 = Content(reverseElements1)

    val bulletPointElementWithoutMarkup = bulletPointElement.copy(text = bulletPointElement.text?.removeSurrounding("~"))
    val codeBlockElementWithoutMarkup = codeBlockElement.copy(text = codeBlockElement.text?.removeSurrounding("`"))

    val content2 = Content(listOf(plaintTextElement, bulletPointElementWithoutMarkup, plaintTextElement, codeBlockElementWithoutMarkup))
    val fullArticleContent = Content(elements1 + elements2)
    val reverseFullArticleContent = Content(reverseElements1 + reverseElements2)

    val category1 = Category("categ1", "Programming", 0xFF00F3)
    val category2 = Category("categ2", "Software Engineering", 0x00FFD4)
    val category3 = Category("categ3", "Design", 0xF0D255)
    val emptyCategory = Category("", "", 0)

    val publisher2 = Publisher(id = "2pub", name = "fake2", emailAddress = "fake2@gmail.com", memberSince = 45555555557785421)
    val publisher1 = Publisher("1pub", "fake1", "fake1@gamil.com", memberSince = 1280282737737,
        followedCategoriesIds = listOf(category1.id, category2.id, category3.id), followedPublishersIds = listOf(publisher2.id))
    val uiPublisher1 = UiPublisher(publisher1, null)
    val uiPublisher2 = UiPublisher(publisher2, null)
    val emptyPublisher = Publisher("", "", "", memberSince = -1)
    val emptyUiPublisher = UiPublisher(emptyPublisher, null)
    val mostFollowedIds = listOf(publisher1.id, publisher2.id, "3publo", "8upolia")

    val comment0 = Comment("6", publisher2.id, "I've replied to you", 787542322223, emptyList())
    val comment1 = Comment("1", publisher1.id, "That's Awesome", 1968764334, emptyList())
    val comment2 = Comment("2", publisher1.id, "Fantastic", 788225123294, listOf(comment0.id))
    val emptyComment = Comment("", "", "", -1, emptyList())
    val comment4 = Comment("", "", "I'm really disagree with you", 0, emptyList())
    var newComment = Comment("adssaffsds", publisher1.id, "This is new comment", 43999999999202, emptyList())

    val comments1 = mutableListOf(comment1, comment2)
    val comment2Replies = mutableListOf(comment0)
    val deFormatTestComment = UiComment(comment4, uiPublisher1, emptyList())
    val newUiComment = UiComment(newComment, uiPublisher1, emptyList())
    val emptyUiComment = UiComment(emptyComment, emptyUiPublisher, emptyList())

    val categories = listOf(category1, category2, category3)
    val categoriesIds = listOf(category1.id, category2.id, category3.id)
    val articleCategories = listOf(category1, category2)

    val article1 = Article(
        "43259253-1pub-arti", "article1", publisher1.id, 2,
        1214343259253, categoryIds = listOf(category1.id, category2.id), numOfAppreciate = 0, numOfDisagree = 0
    )

    val article2 = Article(
        "89479892-2pub-arti", "article2", publisher2.id, 1,
        1529889479892, categoryIds = listOf(category3.id)
    )

    val emptyArticle = Article(
        "", "", emptyPublisher.id, 0, 0, categoryIds = emptyList()
    )
    val emptyUiArticle = UiArticle(emptyArticle, emptyUiPublisher, initialContent = Content(emptyList()), category = emptyList())
    var uiArticle1 = UiArticle(article1, uiPublisher1, initialContent = content1, fullContent = content1, category = listOf(category1, category2))
    val uiArticle2 = UiArticle(article2, uiPublisher2, initialContent = content1, fullContent = content1, category = listOf(category3))
    val uiArticles = listOf(uiArticle1, uiArticle2, emptyUiArticle)
    val deFormatArticleTest = Article("", "article3", publisher2.id, 0, 0, 0, 0, emptyList())
    val uiArticleToBeDeFormatted = UiArticle(deFormatArticleTest, uiPublisher2, content1, reverseFullArticleContent, listOf(category1, category2))

    val articles1 = listOf(article1)
    val articles2 = listOf(article2)
    val articles3 = listOf(article1, article2)
    val articles4 = listOf(article1, article2, emptyArticle)
}
