package hu.bme.sch.cmsch.config

import hu.bme.sch.cmsch.component.app.ExtraMenuEntity
import hu.bme.sch.cmsch.component.app.ExtraMenuRepository
import hu.bme.sch.cmsch.component.debt.ProductEntity
import hu.bme.sch.cmsch.component.debt.ProductRepository
import hu.bme.sch.cmsch.component.debt.ProductService
import hu.bme.sch.cmsch.component.debt.ProductType
import hu.bme.sch.cmsch.component.event.EventEntity
import hu.bme.sch.cmsch.component.event.EventRepository
import hu.bme.sch.cmsch.component.form.FormEntity
import hu.bme.sch.cmsch.component.form.FormRepository
import hu.bme.sch.cmsch.component.form.ResponseEntity
import hu.bme.sch.cmsch.component.form.ResponseRepository
import hu.bme.sch.cmsch.component.news.NewsEntity
import hu.bme.sch.cmsch.component.news.NewsRepository
import hu.bme.sch.cmsch.component.riddle.*
import hu.bme.sch.cmsch.component.staticpage.StaticPageEntity
import hu.bme.sch.cmsch.component.staticpage.StaticPageRepository
import hu.bme.sch.cmsch.component.task.*
import hu.bme.sch.cmsch.component.token.TokenEntity
import hu.bme.sch.cmsch.component.token.TokenRepository
import hu.bme.sch.cmsch.model.*
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.GroupToUserMappingRepository
import hu.bme.sch.cmsch.repository.GuildToUserMappingRepository
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.UserProfileGeneratorService
import hu.bme.sch.cmsch.util.sha256
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

const val LOREM_IPSUM_SHORT_1 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas in justo ac arcu placerat posuere eget at purus. Donec porta lorem in semper semper. Phasellus volutpat sapien et ultricies tristique. In ornare libero vel dignissim ultrices."
const val LOREM_IPSUM_SHORT_2 = "Pellentesque non interdum leo. Mauris egestas augue vel lorem dignissim ullamcorper. Suspendisse tempus ex dolor, in sagittis lorem fermentum ut. Morbi dignissim sollicitudin ornare."
const val LOREM_IPSUM_SHORT_3 = "Donec rutrum enim elit, sed facilisis lectus varius sit amet. Ut vel imperdiet ligula. Phasellus id ullamcorper augue, et varius sapien. Nulla suscipit, nibh nec tristique dapibus, lorem libero facilisis mi, sit amet mattis ligula erat vel massa."
const val LOREM_IPSUM_SHORT_4 = "Duis et lacus ac tellus volutpat lobortis. Curabitur ac sapien vel nibh vestibulum congue vel in purus. In eget leo in nisi lacinia lacinia. Sed tempus arcu non mi iaculis lobortis. Vivamus ultricies sed odio sit amet placerat."

const val LOREM_IPSUM_LONG_1 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas in justo ac arcu placerat posuere eget at purus. Donec porta lorem in semper semper. Phasellus volutpat sapien et ultricies tristique. In ornare libero vel dignissim ultrices. Nullam lacus urna, tristique vitae varius at, gravida eu elit. Ut eget velit porta, finibus elit eu, suscipit quam. Pellentesque lobortis lorem at ligula vulputate sagittis. Aliquam id rhoncus turpis. Ut blandit purus vel est finibus, vitae venenatis massa aliquet. Proin eget luctus odio. Ut tincidunt risus non mi dictum malesuada. Sed tempus massa ligula, vel hendrerit diam porttitor nec. Etiam sem orci, fermentum sit amet lorem at, ullamcorper gravida nisi."
const val LOREM_IPSUM_LONG_2 = "Pellentesque non interdum leo. Mauris egestas augue vel lorem dignissim ullamcorper. Suspendisse tempus ex dolor, in sagittis lorem fermentum ut. Morbi dignissim sollicitudin ornare. Praesent vitae leo in nisi maximus sagittis. Nullam scelerisque lectus non magna faucibus, quis dignissim libero iaculis. Integer porta metus ipsum, eu maximus dui semper ut. Nunc mi erat, feugiat nec orci a, vehicula condimentum mi. Praesent sit amet magna vitae velit blandit pulvinar sit amet eget sem. Aliquam erat volutpat. Mauris justo sapien, congue vitae ligula consequat, imperdiet finibus mi. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos."
const val LOREM_IPSUM_LONG_3 = "Donec rutrum enim elit, sed facilisis lectus varius sit amet. Ut vel imperdiet ligula. Phasellus id ullamcorper augue, et varius sapien. Nulla suscipit, nibh nec tristique dapibus, lorem libero facilisis mi, sit amet mattis ligula erat vel massa. Donec laoreet tortor non turpis molestie vestibulum. Suspendisse neque orci, placerat at porta vitae, ultricies ut libero. Quisque vel arcu ac magna interdum rutrum eget vitae velit. In tempus nunc ligula, ac euismod nisl mollis sit amet. Mauris congue auctor enim et venenatis. Maecenas bibendum erat a egestas auctor. Integer quis efficitur ipsum."
const val LOREM_IPSUM_LONG_4 = "Duis et lacus ac tellus volutpat lobortis. Curabitur ac sapien vel nibh vestibulum congue vel in purus. In eget leo in nisi lacinia lacinia. Sed tempus arcu non mi iaculis lobortis. Vivamus ultricies sed odio sit amet placerat. Praesent turpis est, finibus sit amet malesuada vitae, rhoncus et velit. Vestibulum convallis nibh metus, dictum posuere augue facilisis vel. Aliquam viverra mauris sapien, sed pharetra ante ullamcorper quis. Sed scelerisque nisi nibh, rutrum malesuada elit scelerisque ac. Cras rhoncus magna eu risus mattis, vitae eleifend justo lacinia."

const val LONG_MARKDOWN_DEMO = "# h1 Heading 8-)\n## h2 Heading\n### h3 Heading\n#### h4 Heading\n##### h5 Heading\n###### h6 Heading\n\n\n## Horizontal Rules\n\n___\n\n---\n\n***\n\n\n## Typographic replacements\n\nEnable typographer option to see result.\n\n(c) (C) (r) (R) (tm) (TM) (p) (P) +-\n\ntest.. test... test..... test?..... test!....\n\n!!!!!! ???? ,,  -- ---\n\nSmartypants, double quotes\" and 'single quotes'\n\n\n## Emphasis\n\n**This is bold text**\n\n__This is bold text__\n\n*This is italic text*\n\n_This is italic text_\n\n~~Strikethrough~~\n\n\n## Blockquotes\n\n\n> Blockquotes can also be nested...\n>> ...by using additional greater-than signs right next to each other...\n> > > ...or with spaces between arrows.\n\n\n" +
        "## Lists\n\nUnordered\n\n+ Create a list by starting a line with `+`, `-`, or `*`\n+ Sub-lists are made by indenting 2 spaces:\n- Marker character change forces new list start:\n* Ac tristique libero volutpat at\n+ Facilisis in pretium nisl aliquet\n- Nulla volutpat aliquam velit\n+ Very easy!\n\nOrdered\n\n1. Lorem ipsum dolor sit amet\n2. Consectetur adipiscing elit\n3. Integer molestie lorem at massa\n\n\n1. You can use sequential numbers...\n1. ...or keep all the numbers as `1.`\n\nStart numbering with offset:\n\n57. foo\n1. bar\n\n\n## Code\n\nInline `code`\n\nIndented code\n\n// Some comments\nline 1 of code\nline 2 of code\nline 3 of code\n\n\nBlock code \"fences\"\n\n```\nSample text here...\n```\n\nSyntax highlighting\n\n```" +
        " js\nvar foo = function (bar) {\nreturn bar++;\n};\n\nconsole.log(foo(5));\n```\n\n## Tables\n\n| Option | Description |\n| ------ | ----------- |\n| data   | path to data files to supply the data that will be passed into templates. |\n| engine | engine to be used for processing templates. Handlebars is the default. |\n| ext    | extension to be used for dest files. |\n\nRight aligned columns\n\n| Option | Description |\n| ------:| -----------:|\n| data   | path to data files to supply the data that will be passed into templates. |\n| engine | engine to be used for processing templates. Handlebars is the default. |\n| ext    | extension to be used for dest files. |\n\n\n## Links\n\n[link text](http://dev.nodeca.com)\n\n" +
        "[link with title](http://nodeca.github.io/pica/demo/ \"title text!\")\n\nAutoconverted link https://github.com/nodeca/pica (enable linkify to see)\n\n\n## Images\n\n![Minion](https://octodex.github.com/images/minion.png)\n![Stormtroopocat](https://octodex.github.com/images/stormtroopocat.jpg \"The Stormtroopocat\")\n\nLike links, Images also have a footnote style syntax\n\n![Alt text][id]\n\nWith a reference later in the document defining the URL location:\n\n[id]: https://octodex.github.com/images/dojocat.jpg  \"The Dojocat\"\n\n\n## Plugins\n\nThe killer feature of `markdown-it` is very effective support of\n[syntax plugins](https://www.npmjs.org/browse/keyword/markdown-it-plugin).\n\n\n" +
        "### [Emojies](https://github.com/markdown-it/markdown-it-emoji)\n\n> Classic markup: :wink: :crush: :cry: :tear: :laughing: :yum:\n>\n> Shortcuts (emoticons): :-) :-( 8-) ;)\n\nsee [how to change output](https://github.com/markdown-it/markdown-it-emoji#change-output) with twemoji.\n\n\n### [Subscript](https://github.com/markdown-it/markdown-it-sub) / [Superscript](https://github.com/markdown-it/markdown-it-sup)\n\n- 19^th^\n- H~2~O\n\n\n### [\\<ins>](https://github.com/markdown-it/markdown-it-ins)\n\n++Inserted text++\n\n\n### [\\<mark>](https://github.com/markdown-it/markdown-it-mark)\n\n==Marked text==\n\n\n### [Footnotes](https://github.com/markdown-it/markdown-it-footnote)\n\nFootnote 1 link[^first].\n\nFootnote 2 link[^second].\n\n" +
        "Inline footnote^[Text of inline footnote] definition.\n\nDuplicated footnote reference[^second].\n\n[^first]: Footnote **can have markup**\n\nand multiple paragraphs.\n\n[^second]: Footnote text.\n\n\n### [Definition lists](https://github.com/markdown-it/markdown-it-deflist)\n\nTerm 1\n\n:   Definition 1\nwith lazy continuation.\n\nTerm 2 with *inline markup*\n\n:   Definition 2\n\n{ some code, part of Definition 2 }\n\nThird paragraph of definition 2.\n\n_Compact style:_\n\nTerm 1\n~ Definition 1\n\nTerm 2\n~ Definition 2a\n~ Definition 2b\n\n\n### [Abbreviations](https://github.com/markdown-it/markdown-it-abbr)\n\nThis is HTML abbreviation example.\n\nIt converts \"HTML\", but keep intact partial entries like \"xxxHTMLyyy\" and so on.\n\n" +
        "* [HTML]: Hyper Text Markup Language\n\n### [Custom containers](https://github.com/markdown-it/markdown-it-container)\n\n::: warning\n*here be dragons*\n:::\n"

const val A_DAY = 60 * 60 * 24

@Profile("test")
@Configuration
open class TestConfig(
    private val users: UserRepository,
    private val groups: GroupRepository,
    private val groupToUserMapping: GroupToUserMappingRepository,
    private val guildToUserMapping: GuildToUserMappingRepository,
    private val profileService: UserProfileGeneratorService,
    private val news: Optional<NewsRepository>,
    private val events: Optional<EventRepository>,
    private val tasks: Optional<TaskEntityRepository>,
    private val submittedTasks: Optional<SubmittedTaskRepository>,
    private val categories: Optional<TaskCategoryRepository>,
    private val extraPages: Optional<StaticPageRepository>,
    private val products: Optional<ProductRepository>,
    private val productsService: Optional<ProductService>,
    private val riddleRepository: Optional<RiddleEntityRepository>,
    private val riddleCategoryRepository: Optional<RiddleCategoryRepository>,
    private val tokenRepository: Optional<TokenRepository>,
    private val formRepository: Optional<FormRepository>,
    private val formResponseRepository: Optional<ResponseRepository>,
    private val extraMenuRepository: ExtraMenuRepository,
    private val riddleCacheManager: RiddleCacheManager
) {

    private var now = System.currentTimeMillis() / 1000
    private var user1: UserEntity? = null
    private var inited: Boolean = false

    @Transactional(isolation = Isolation.SERIALIZABLE)
    open fun init() {
        if (users.findAll().toList().isNotEmpty())
            return

        news.ifPresent { addNews(it) }
        events.ifPresent { addEvents(it) }
        addGroups()
        addUsers()
        tasks.ifPresent { task ->
            submittedTasks.ifPresent { submitted ->
                categories.ifPresent { category ->
                    addTasks(task, submitted, category)
                }
            }
        }
        products.ifPresent { product ->
            productsService.ifPresent { productService ->
                addProducts(product, productService)
            }
        }
        extraPages.ifPresent { addExtraPages(it) }
        addGroupMapping()
        addGuildMappings()
        riddleRepository.ifPresent { riddle ->
            riddleCategoryRepository.ifPresent { category ->
                addRiddles(riddle, category)
            }
        }
        tokenRepository.ifPresent { addTokens(it) }
        addExtraMenus()
        formRepository.ifPresent { form ->
            formResponseRepository.ifPresent { response ->
                addForms(form, response)
            }
        }
    }

    @Scheduled(fixedDelay = 3000L)
    open fun delayedInit() {
        if (inited)
            return
        inited = true
        riddleCacheManager.resetCache(persistMapping = false, overrideMappings = false)
    }

    private fun addForms(form: FormRepository, response: ResponseRepository) {
        val formEntity = FormEntity(
            0,
            "Teszt Form",
            "test-from",
            "Form",
            "[{\"fieldName\":\"phone\",\"label\":\"Telefonszám\",\"type\":\"PHONE\",\"formatRegex\":\".*\",\"invalidFormatMessage\":\"\",\"values\":\"\",\"note\":\"\",\"required\":true,\"permanent\":true},{\"fieldName\":\"allergy\",\"label\":\"Étel érzékenység\",\"type\":\"SELECT\",\"formatRegex\":\".*\",\"invalidFormatMessage\":\"\",\"values\":\"Nincs, Glutén, Laktóz, Glutés és laktóz\",\"note\":\"Ha egyéb is van, kérem írja megjegyzésbe\",\"required\":true,\"permanent\":true},{\"fieldName\":\"love-trains\",\"label\":\"Szereted a mozdonyokat?\",\"type\":\"CHECKBOX\",\"formatRegex\":\".*\",\"invalidFormatMessage\":\"\",\"values\":\"\",\"note\":\"\",\"required\":true,\"permanent\":true},{\"fieldName\":\"warn1\",\"label\":\"FIGYELEM\",\"type\":\"WARNING_BOX\",\"formatRegex\":\".*\",\"invalidFormatMessage\":\"\",\"values\":\"\",\"note\":\"Ha nem szereti a mozdonyokat, akkor nagyon kellemetlen élete lesz magának kolléga!\",\"required\":false,\"permanent\":false},{\"fieldName\":\"text1\",\"label\":\"Szabályzat\",\"type\":\"TEXT_BOX\",\"formatRegex\":\".*\",\"invalidFormatMessage\":\"\",\"values\":\"A tábor szabályzata itt olvasható: https://szabalyzat.ssl.nincs.ilyen.domain.hu/asdasdasd/kutya\",\"note\":\"\",\"required\":false,\"permanent\":false},{\"fieldName\":\"agree\",\"label\":\"A szabályzatot elfogadom\",\"type\":\"MUST_AGREE\",\"formatRegex\":\".*\",\"invalidFormatMessage\":\"\",\"values\":\"\",\"note\":\"Különben nem jöhet am\",\"required\":false,\"permanent\":false},{\"fieldName\":\"food\",\"label\":\"Mit enne?\",\"type\":\"SELECT\",\"formatRegex\":\".*\",\"invalidFormatMessage\":\"\",\"values\":\"Gyros tál, Brassói, Pho Leves\",\"note\":\"Első napi kaja\",\"required\":true,\"permanent\":true}]",
            RoleType.BASIC,
            RoleType.SUPERUSER,
            "form submitted",
            "form accepted",
            true,
            now - 1000,
            now + A_DAY,
            true,
            2,
            true
        )
        form.save(formEntity)

        response.save(ResponseEntity(
            0,
            user1?.id,
            user1?.fullName ?: "",
            null,
            "",
            formEntity.id,
            now,
            0,
            false,
            0,
            false,
            "",
            user1?.email ?: "n/a",
            "{\"field\":\"val\"}"
        ))
    }

    private fun addTokens(tokenRepository: TokenRepository) {
        tokenRepository.save(TokenEntity(
            0,
            "Kir-Dev",
            "A5BCD8242".sha256(),
            true,
            "default"
        ))
        tokenRepository.save(TokenEntity(
            0,
            "Invisible token",
            "XDDD".sha256(),
            false,
            "default"
        ))
        tokenRepository.save(TokenEntity(
            0,
            "NFT-sch kör",
            "NFT".sha256(),
            true,
            "default"
        ))
        tokenRepository.save(TokenEntity(
            0,
            "Crypto Reszort",
            "crypto".sha256(),
            true,
            "default"
        ))
        tokenRepository.save(TokenEntity(
            0,
            "Kollégiumi Szak-Kollégium (KSZK)",
            "kszk".sha256(),
            true,
            "default"
        ))
        tokenRepository.save(TokenEntity(
            0,
            "Extra Token",
            "x".sha256(),
            true,
            "extra"
        ))
    }

    private fun addRiddles(riddleRepository: RiddleEntityRepository, riddleCategoryRepository: RiddleCategoryRepository) {
        riddleCategoryRepository.save(RiddleCategoryEntity(
            0,
            "Általános",
            1,
            true,
            RoleType.BASIC
        ))
        riddleCategoryRepository.save(RiddleCategoryEntity(
            0,
            "Kir-Dev speciál",
            2,
            true,
            RoleType.BASIC
        ))
        riddleCategoryRepository.save(RiddleCategoryEntity(
            0,
            "Üres riddleök",
            5,
            true,
            RoleType.BASIC
        ))
        riddleCategoryRepository.save(RiddleCategoryEntity(
            0,
            "Nem látható",
            3,
            false,
            RoleType.BASIC
        ))
        riddleCategoryRepository.save(RiddleCategoryEntity(
            0,
            "Admin riddleök",
            6,
            true,
            RoleType.ADMIN
        ))

        riddleRepository.save(RiddleEntity(
            0,
            "Első riddle",
            "/image1.png",
            "elso",
            "Az első szám a számegyenesen",
            10,
            1,
            1
        ))
        riddleRepository.save(RiddleEntity(
            0,
            "Második riddle",
            "/image2.png",
            "masodik",
            "A második szám a számegyenesen",
            10,
            2,
            1
        ))
        riddleRepository.save(RiddleEntity(
            0,
            "Harmadik riddle",
            "/image3.png",
            "HarMadik",
            "Szedd össze magad!",
            20,
            3,
            1
        ))


        riddleRepository.save(
            RiddleEntity(
            0,
            "Kir-Dev riddle",
            "/image4.png",
            "Kir-Dev",
            "Beszoptad tesóm",
            100,
            1,
            2
        )
        )
        riddleRepository.save(
            RiddleEntity(
            0,
            "Admin riddle",
            "/image5.png",
            "Admin",
            "Admin Riddle",
            200,
            1,
            6
        )
        )
    }

    private fun addGroups() {
        groups.save(GroupEntity(
                name = "V10",
                major = MajorType.EE,
                staff1 = "Kelep Elek| fb.com/kelep| +36301002000",
                staff2 = "Kelep Kelep| fb.com/dzsinks142| +36301002001",
                staff3 = "Elek Elek| fb.com/elek| +36301002002",
                staff4 = "Het Golya| fb.com/nem| +36301002003",
                races = true,
                selectable = true,
                leaveable = false
        ))

        groups.save(GroupEntity(
                name = "I16",
                major = MajorType.IT,
                staff1 = "Tony Stork| fb.com/kelep| +36301003000",
                staff2 = "Tony Montana| fb.com/dzsinks142| +36301003001",
                staff3 = "Tony Hilfiger| fb.com/elek| +36301003002",
                staff4 = "Bekő Tony| fb.com/nem| +36301003003",
                races = true,
                selectable = true,
                leaveable = false
        ))

        groups.save(GroupEntity(
                name = "I09",
                major = MajorType.IT,
                staff1 = "Nem Mindegy| fb.com/kelep| +36301003000",
                staff2 = "De Mindegy| fb.com/dzsinks142| +36301003001",
                staff3 = "Tony Hilfiger| fb.com/elek| +36301003002",
                staff4 = "Bekő Tony| fb.com/nem| +36301003003",
                races = true,
                selectable = true,
                leaveable = false
        ))

        groups.save(GroupEntity(
            name = "Vendég",
            major = MajorType.UNKNOWN,
            staff1 = "",
            staff2 = "",
            staff3 = "",
            staff4 = "",
            races = false,
            selectable = true,
            leaveable = false
        ))

        groups.save(GroupEntity(
            name = "Kiállító",
            major = MajorType.UNKNOWN,
            staff1 = "",
            staff2 = "",
            staff3 = "",
            staff4 = "",
            races = false,
            selectable = false,
            leaveable = false
        ))
    }

    private fun addNews(news: NewsRepository) {
        news.save(NewsEntity(title = "Az eslő hír",
                content = LOREM_IPSUM_SHORT_1,
                visible = true, highlighted = false
        ))
        news.save(NewsEntity(title = "A második highlightolt hír",
                content = LOREM_IPSUM_SHORT_2,
                visible = true, highlighted = true
        ))
        news.save(NewsEntity(title = "Ez nem is hír, nem látszik",
                content = LOREM_IPSUM_SHORT_3,
                visible = false, highlighted = false
        ))
        news.save(NewsEntity(title = "Teszt hír 4",
                content = LOREM_IPSUM_SHORT_4,
                visible = true, highlighted = false
        ))
        news.save(NewsEntity(title = "Teszt hír 5",
                content = LOREM_IPSUM_SHORT_3,
                visible = true, highlighted = false
        ))
        news.save(NewsEntity(title = "Markdown hír",
                content = LONG_MARKDOWN_DEMO,
                visible = true, highlighted = false
        ))
    }

    private fun addEvents(events: EventRepository) {
        events.save(EventEntity(
                url = "hetfoi-elso-program",
                title = "Hétfői Első Program",
                category = "Egyetemi",
                place = "Schönherz",
                timestampStart = now - A_DAY,
                timestampEnd = now - A_DAY + (3 * 3600),
                previewDescription = LOREM_IPSUM_SHORT_1,
                description = LOREM_IPSUM_LONG_1,
                visible = true,
                extraButtonTitle = "",
                extraButtonUrl = ""
        ))

        events.save(EventEntity(
                url = "hetfoi-masodik-program",
                title = "Hétfői Második Program",
                category = "Szórakozás",
                place = "Schönherz",
                timestampStart = now - A_DAY + (3600),
                timestampEnd = now - A_DAY + (3 * 3600),
                previewDescription = LOREM_IPSUM_SHORT_2,
                description = LOREM_IPSUM_LONG_2,
                visible = true,
                extraButtonTitle = "BUTTON TEXT",
                extraButtonUrl = "http://example.com/target"
        ))

        events.save(EventEntity(
                url = "hetfoi-tiltott-program",
                title = "Hétfői Tiltott Program",
                category = "Öhömm",
                place = "Lovagterem",
                timestampStart = now - A_DAY + (5 * 3600),
                timestampEnd = now - A_DAY + (12 * 3600),
                previewDescription = "Ennek nem kellene látszani",
                description = "Ennek nem kellene látszani (full leírás)",
                visible = false,
                extraButtonTitle = "",
                extraButtonUrl = ""
        ))

        events.save(EventEntity(
                url = "kedd-elso-program",
                title = "Kedden Volt",
                category = "Szórakozás",
                place = "Schönherz",
                timestampStart = now - (3600),
                timestampEnd = now - (100),
                previewDescription = LOREM_IPSUM_SHORT_2,
                description = LOREM_IPSUM_LONG_2,
                visible = true,
                extraButtonTitle = "",
                extraButtonUrl = ""
        ))

        events.save(EventEntity(
                url = "fene-kedd",
                title = "Fene Kedd",
                category = "Szórakozás",
                place = "Feneketlen tó",
                timestampStart = now,
                timestampEnd = now + A_DAY,
                previewDescription = LOREM_IPSUM_SHORT_3,
                description = LOREM_IPSUM_LONG_3,
                visible = true,
                extraButtonTitle = "",
                extraButtonUrl = ""
        ))

        events.save(EventEntity(
                url = "kopa-szi-get",
                title = "Kopa-Sziget",
                category = "Bulika",
                place = "Schönherz",
                timestampStart = now + (4 * 3600),
                timestampEnd = now + (4 * 3600) + A_DAY,
                previewDescription = LOREM_IPSUM_SHORT_3,
                description = LOREM_IPSUM_LONG_3,
                visible = true,
                extraButtonTitle = "",
                extraButtonUrl = ""
        ))

        events.save(EventEntity(
                url = "watt-fesztivál",
                title = "Watt",
                category = "Egyetemi",
                place = "Drop-szerda klub",
                timestampStart = now + A_DAY,
                timestampEnd = now + A_DAY + 30,
                previewDescription = LOREM_IPSUM_SHORT_1,
                description = LOREM_IPSUM_LONG_1,
                visible = true,
                extraButtonTitle = "",
                extraButtonUrl = ""
        ))

        events.save(EventEntity(
                url = "bika-sound",
                title = "Bika Sound",
                category = "Szórakozás",
                place = "Bikás park",
                timestampStart = now - A_DAY + (3600),
                timestampEnd = now - A_DAY + (2 * 3600),
                previewDescription = LOREM_IPSUM_SHORT_2,
                description = LOREM_IPSUM_LONG_2,
                visible = true,
                extraButtonTitle = "BUTTON TEXT 2",
                extraButtonUrl = "http://example.com/target2"
        ))
    }

    private fun addTasks(tasks: TaskEntityRepository, submittedTasks: SubmittedTaskRepository, categories: TaskCategoryRepository) {
        val achi1 = TaskEntity(
                title = "Merre van balra?",
                expectedResultDescription = "Egy kép arról mere van balra",
                categoryId = 1,
                visible = true,
                order = 1,
                availableFrom = now - (12 * A_DAY),
                availableTo = now + (4 * A_DAY),
                type = TaskType.TEXT,
                format = TaskFormat.TEXT,
                maxScore = 50,
                description = LOREM_IPSUM_LONG_1
        )
        tasks.save(achi1)

        val achi11 = TaskEntity(
                title = "Kód beadás",
                expectedResultDescription = "valami kód",
                categoryId = 1,
                visible = true,
                order = 1,
                availableFrom = now - (12 * A_DAY),
                availableTo = now + (4 * A_DAY),
                type = TaskType.TEXT,
                format = TaskFormat.CODE,
                maxScore = 50,
                description = LOREM_IPSUM_LONG_1
        )
        tasks.save(achi11)

        val achi12 = TaskEntity(
                title = "Pdf beadás",
                expectedResultDescription = "valami pdf",
                categoryId = 1,
                visible = true,
                order = 1,
                availableFrom = now - (12 * A_DAY),
                availableTo = now + (4 * A_DAY),
                type = TaskType.ONLY_PDF,
                maxScore = 50,
                description = LOREM_IPSUM_LONG_1
        )
        tasks.save(achi12)

        val achi13 = TaskEntity(
                title = "Custom form",
                expectedResultDescription = "töltsd ki a formot",
                categoryId = 1,
                visible = true,
                order = 1,
                availableFrom = now - (12 * A_DAY),
                availableTo = now + (4 * A_DAY),
                type = TaskType.TEXT,
                format = TaskFormat.FORM,
                formatDescriptor = """[{"title": "Név","type":"text"},{"title": "Testmagasság","type":"number","suffix":"cm"},{"title": "Magadról","type":"textarea"}]""",
                maxScore = 50,
                description = LOREM_IPSUM_LONG_1
        )
        tasks.save(achi13)

        val achi2 = TaskEntity(
                title = "Milyen 'Jé' a kamion?",
                expectedResultDescription = "Egy fotó a kamilyonról",
                categoryId = 2,
                visible = true,
                order = 2,
                availableFrom = now - (3 * A_DAY),
                availableTo = now + (2 * A_DAY),
                type = TaskType.IMAGE,
                maxScore = 150,
                description = LOREM_IPSUM_LONG_2
        )
        tasks.save(achi2)

        val achi3 = TaskEntity(
                title = "Valami vicces megjegyzés az egyik gólyalányról",
                expectedResultDescription = "Milyen szinű és miért kék?",
                categoryId = 1,
                visible = false,
                order = 3,
                availableFrom = now - (3 * A_DAY),
                availableTo = now + (2 * A_DAY),
                type = TaskType.TEXT,
                format = TaskFormat.TEXT,
                maxScore = 69,
                description = "Úgy sem látszik"
        )
        tasks.save(achi3)

        tasks.save(
            TaskEntity(
                title = "Milyen lóról nevezték el a lóvagtermet?",
                expectedResultDescription = "A ló neve kisbetűvel",
                categoryId = 2,
                visible = true,
                order = 3,
                availableFrom = now - (3 * A_DAY),
                availableTo = now + (2 * A_DAY),
                type = TaskType.BOTH,
                format = TaskFormat.TEXT,
                maxScore = 30,
                description = LOREM_IPSUM_LONG_3
        )
        )

        tasks.save(
            TaskEntity(
                title = "Mingyá' lejár",
                expectedResultDescription = "Kép a centrifugáról (am ez lejárt)",
                categoryId = 3,
                visible = true,
                order = 2,
                availableFrom = now - (3 * A_DAY),
                availableTo = now - (2 * A_DAY),
                type = TaskType.IMAGE,
                maxScore = 420,
                description = "Ez lejárt"
        )
        )

        val achi4 = TaskEntity(
                title = "Mit mér a mérnök?",
                expectedResultDescription = "asszem sört, na mérjetek sört",
                categoryId = 1,
                visible = true,
                order = 4,
                availableFrom = now - (3 * A_DAY),
                availableTo = now + (2 * A_DAY),
                type = TaskType.IMAGE,
                maxScore = 150,
                description = LOREM_IPSUM_LONG_4
        )
        tasks.save(achi4)

        val achi5 = TaskEntity(
            title = "Profilkép",
            expectedResultDescription = "kép a fejedről",
            categoryId = 4,
            visible = true,
            order = 4,
            availableFrom = now - (3 * A_DAY),
            availableTo = now + (2 * A_DAY),
            type = TaskType.IMAGE,
            maxScore = 150,
            description = LOREM_IPSUM_LONG_4
        )
        tasks.save(achi5)


        tasks.save(
            TaskEntity(
                title = "Milye van a fának?",
                expectedResultDescription = "gráfelméleti tézis",
                categoryId = 2,
                visible = true,
                order = 4,
                availableFrom = now - (3 * A_DAY),
                availableTo = now + (2 * A_DAY),
                type = TaskType.TEXT,
                format = TaskFormat.TEXT,
                maxScore = 150,
                description = "Levele van, vagy egyel több csúcsa mint éle?"
        )
        )

        val groupI16 = groups.findByName("I16").orElseThrow()
        val groupI09 = groups.findByName("I09").orElseThrow()
        val groupV10 = groups.findByName("V10").orElseThrow()

        submittedTasks.save(
            SubmittedTaskEntity(
                0,
                achi1,
                groupI16.id,
                "I16",
                null,
                "",
                2,
                "Ezt adtuk be xd",
                "",
                "",
                "Hát kár volt bazdmeg",
                approved = false,
                rejected = true,
                score = 0
        )
        )

        submittedTasks.save(
            SubmittedTaskEntity(
                0,
                achi1,
                groupI09.id,
                "I09",
                null,
                "",
                1,
                "Szia Lajos!",
                "",
                "",
                "Szia Bazdmeg!",
                approved = true,
                rejected = false,
                score = 20
        )
        )

        submittedTasks.save(
            SubmittedTaskEntity(
                0,
                achi1,
                groupV10.id,
                "V10",
                null,
                "",
                1,
                "Jobbra",
                "",
                "",
                "",
                approved = false,
                rejected = false,
                score = 0
        )
        )

        submittedTasks.save(
            SubmittedTaskEntity(
                0,
                achi2,
                groupI16.id,
                "I16",
                null,
                "",
                2,
                "Ellipszilonos",
                "",
                "",
                "",
                approved = false,
                rejected = false,
                score = 0
        )
        )

        submittedTasks.save(
            SubmittedTaskEntity(
                0,
                achi2,
                groupI09.id,
                "I09",
                null,
                "",
                1,
                "",
                "task/test.png",
                "",
                "",
                approved = false,
                rejected = false,
                score = 0
        ))

        categories.save(TaskCategoryEntity(0, "Mine Category", 1, 0, 3000000000))
        categories.save(
            TaskCategoryEntity(
                name = "Mine Category2",
                categoryId = 2,
                availableFrom = 0,
                availableTo = 3000000000
        ))
        categories.save(
            TaskCategoryEntity(
            name = "Mine Category3",
            categoryId = 3,
            availableFrom = 0,
            availableTo = 3000000000
        ))
        categories.save(
            TaskCategoryEntity(
                name = "muszájj",
                categoryId = 4,
                availableFrom = 0,
                availableTo = 3000000000,
                type = TaskCategoryType.PROFILE_REQUIRED
            ))
    }

    private fun addUsers() {
        user1 = UserEntity(
                internalId = UUID.randomUUID().toString(),
                neptun = "HITMAN",
                email = "hitman@beme.hu",
                major = MajorType.EE,
                role = RoleType.BASIC,
                fullName = "Hitman János",
                guild = GuildType.YELLOW,
                groupName = "V10",
                group = groups.findByName("V10").orElse(null)
        )
        profileService.generateFullProfileForUser(user1!!)
        users.save(user1!!)

        val u2 = UserEntity(
                internalId = UUID.randomUUID().toString(),
                neptun = "BATMAN",
                email = "batman@beme.hu",
                major = MajorType.IT,
                role = RoleType.BASIC,
                fullName = "Bat Man",
                guild = GuildType.RED,
                groupName = "V10",
                group = groups.findByName("V10").orElse(null)
        )
        profileService.generateFullProfileForUser(u2)
        users.save(u2)

        val u3 = UserEntity(
                internalId = UUID.randomUUID().toString(),
                neptun = "FITYMA",
                email = "fityma@beme.hu",
                major = MajorType.BPROF,
                role = RoleType.BASIC,
                fullName = "Fitty Mátyás",
                guild = GuildType.BLACK
        )
        profileService.generateFullProfileForUser(u3)
        users.save(u3)

        val random = Random()
        for (i in 0..2000) {
            val un = UserEntity(
                internalId = UUID.randomUUID().toString(),
                neptun = "NE" + i,
                email = "${random.nextLong()}@beme.hu",
                major = MajorType.BPROF,
                role = RoleType.BASIC,
                fullName = "Random Npc $i",
                guild = GuildType.BLACK,
                config = "c" + i,
                cmschId = "cmsch" + i,
                profileTopMessage = "msg${random.nextLong()}-${random.nextLong()}-${random.nextLong()}${random.nextLong()}",
                permissions = "PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,PERMISSION_GOES_HERE,$i"
            )
            users.save(un)
        }
    }

    private fun addProducts(products: ProductRepository, productsService: ProductService) {
        val product1 = ProductEntity(
                name = "G7 Repohár",
                description = "Feelinges repohár, amiből lehet inni is meg enni is.",
                price = 300,
                type = ProductType.MERCH,
                available = true,
                visible = true,
        )
        products.save(product1)

        products.save(ProductEntity(
                name = "G7 Traktorgumi",
                description = "Ritmikus földhözveréshez.",
                price = 32000,
                type = ProductType.MERCH,
                available = false,
                visible = false,
        ))

        products.save(ProductEntity(
                name = "G7 Kulacs",
                description = "Leginkább folyadék tárolására alkalmas zárható tároló alkalmatosság.",
                price = 550,
                type = ProductType.MERCH,
                available = false,
                visible = true,
        ))

        products.save(ProductEntity(
                name = "G7 Folt",
                description = "Pulcsira lehet felvarrni amúgy.",
                price = 200,
                type = ProductType.MERCH,
                available = true,
                visible = true,
        ))

        products.save(ProductEntity(
                name = "Hétfői kaja",
                description = "CSB",
                price = 550,
                type = ProductType.FOOD,
                available = true,
                visible = false,
        ))

        val product2 = ProductEntity(
                name = "Keddi kaja",
                description = "Chili con carne",
                price = 550,
                type = ProductType.FOOD,
                available = true,
                visible = false,
        )
        products.save(product2)

        products.save(ProductEntity(
                name = "Szerdai kaja",
                description = "Ettemma",
                price = 550,
                type = ProductType.FOOD,
                available = true,
                visible = false,
        ))

        val user1 = users.findByNeptun("BATMAN").orElseThrow()
        val user2 = users.findByNeptun("HITMAN").orElseThrow()
        val merchant = users.findByNeptun("FITYMA").orElseThrow()

        productsService.sellProductByCmschId(product1.id, merchant, user1.cmschId)
        productsService.sellProductByNeptun(product2.id, merchant, user1.neptun)
        productsService.sellProductByCmschId(product1.id, merchant, user2.cmschId)
        productsService.sellProductByNeptun(product1.id, merchant, user2.neptun)
        productsService.sellProductByCmschId(product1.id, merchant, user2.cmschId)
    }

    private fun addExtraPages(extraPages: StaticPageRepository) {
        extraPages.save(StaticPageEntity(
                title = "Gyakran Ismételt Kérdések",
                url = "gyik",
                visible = true,
                open = true,
                permissionToEdit = "EXTRAPAGE_EDIT_GYIK",
                showAsMenu = true,
                menuTitle = "GYIK",
                content = "Gyakran Ismételt Kérdések\n" +
                        "===\n" +
                        "\n" +
                        "## Hány laci kell egy villanykörte cseréhez?\n" +
                        "\n" +
                        "Egy laci kell egy villanykörte cseréhez.\n" +
                        "\n" +
                        "## Mifajta kutya ez?\n" +
                        "\n" +
                        "Ez egy teknős páncélban.\n" +
                        "\n" +
                        "## Mi az amit te keresel?\n" +
                        "\n" +
                        "Na ezt most ne, pls!\n",
            ogDescription = "Ez a GYIK oldal, ide jön az open graph leírás",
            ogImage = "https://dummyimage.com/940x768/ffffff/000000.png",
            ogTitle = "CMSCH - GYIK"
        ))

        extraPages.save(StaticPageEntity(
                title = "Egy másik nemzedék",
                url = "egy-masik-nemzedek",
                visible = false,
                open = true,
                permissionToEdit = "EXTRAPAGE_EDIT_OTHER",
                showAsMenu = false,
                content = "No Thanx - Egy masik nemzedek\n" +
                        "===\n" +
                        "\n" +
                        "> Minden fejre áll holnap,\n> Ránk már senki sem szólhat,\n> Nem kell többé a szent beszéd.\n> Ez egy másik nemzedék!\n> Mondtuk szépen és durván,\n> Lelkünk ébredő vulkán kitörni kész,\n> S ami régi mind elég s jön az újabb nemzedék\n> Évek óta úgy nevelsz, hogy mit szabad s miért\n> Hogy mitől lenne jó, hogy miért ne hulljunk szét\n> De hagyjuk ezt a rossz dumát,\n> Hasztalan a szó, úgyse látsz belénk, mi tudjuk mi a jó,\n" +
                        "> Mert mi vagyunk a hurrikán,\n> Ezeréves út után mindent felkavar\n> Készülj fel, most mi jövünk!\n> Minden fejre áll holnap,\n> Ránk már senki sem szólhat\n> Nem kell többé a szentbeszéd\n> Ez egy másik nemzedék\n> Mondtuk szépen és durván\n> Lelkünk ébredő vulkán kitörni kész s ami régi! mind elég,\n> S jön az újabb nemzedék\n> Ez egy másik nemzedék\n> Nézek rád és nem hiszem,\n> Hogy ennyi jár csupán\n> Hogy hajtasz mint a gép,\n" +
                        "> és futsz a pénz után\n> Élnek még az álmaink\n> Hogy minket várt a föld\n> Törjük össze hát, mi téged már megölt\n> Mert mi vagyunk a hurrikán\n> Ezer éves út után mindent felkavar\n> Készülj fel most mi jövünk\n> Minden fejre áll holnap\n> Ránk már senki sem szólhat\n> Nem kell többé a szentbeszéd,\n> Ez egy másik nemzedék\n> Mondtuk szépen és durván,\n> Lelkünk ébredő vulkán\n> Kitörni kész s ami régi mind elég\n> S jön az újabb nemzedék\n" +
                        "> Ez egy másik nemzedék\n> Mert mi vagyunk a hurrikán,\n> Ezeréves út után mindent felkavar\n> Készülj fel, most mi jövünk!\n> Minden fejre áll holnap,\n> Ránk már senki sem szólhat\n> Nem kell többé a szentbeszéd\n> Ez egy másik nemzedék\n> Mondtuk szépen és durván\n> Lelkünk ébredő vulkán kitörni kész s ami régi mind elég,\n> S jön az újabb nemzedék\n> Minden fejre áll holnap\n> Ránk már senki sem szólhat\n> Nem kell többé a szent beszéd\n" +
                        "> Ez egy másik nemzedék\n> Mondtuk szépen és durván lelkünk ébredő vulkán\n> Kitörni kész s ami régi mind elég\n> S jön az újabb nemzedék\n> Minden fejre áll holnap\n> Ránk már senki sem szólhat\n> Nem kell többé a szent beszéd,\n> Ez egy másik nemzedék\n> Mondtuk szépen és durván\n> Lelkünk ébredő vulkán\n> Kitörni kész s ami régi mind elég\n> S jön az újabb nemzedék\n> Minden fejre áll holnap,\n> Ránk már senki sem szólhat\n" +
                        "> Nem kell többé a szent beszéd\n> Ez egy másik nemzedék\n"
        ))

        extraPages.save(StaticPageEntity(
                title = "Az idei G7 költségvetése",
                url = "koltsegvetes",
                visible = false,
                open = false,
                showAsMenu = false,
                menuTitle = "Költségvetés",
                permissionToEdit = "EXTRAPAGE_EDIT_OTHER",
                content = "Az idei G7 költésgvetése\n" +
                        "===\n" +
                        "\n" +
                        "Ja persze, majd ideírjuk...\n"
        ))

        extraPages.save(StaticPageEntity(
                title = "Telejsen átlagos oldal",
                url = "atlagos-oldal",
                visible = true,
                open = true,
                showAsMenu = true,
                menuTitle = "Átlag Oldal",
                content = "Ez egy átlagos oldal\n" +
                        "===\n" +
                        "\n" +
                        "Teljesen **átlagos**!\n"
        ))
    }

    private fun addGroupMapping() {
        groupToUserMapping.save(GroupToUserMappingEntity(0, "HITMAN", "Hitman Lajos", "V10", MajorType.EE))
        groupToUserMapping.save(GroupToUserMappingEntity(0, "BATMAN", "Batman Bálint", "V10", MajorType.EE))
        groupToUserMapping.save(GroupToUserMappingEntity(0, "RZPZTT", "Mottomén Kiez", "I09", MajorType.IT))
    }

    private fun addGuildMappings() {
        guildToUserMapping.save(GuildToUserMappingEntity(0, "RZPZTT", GuildType.RED))
        guildToUserMapping.save(GuildToUserMappingEntity(0, "HITMAN", GuildType.WHITE))
        guildToUserMapping.save(GuildToUserMappingEntity(0, "BATMAN", GuildType.BLACK))
    }

    private fun addExtraMenus() {
        extraMenuRepository.save(ExtraMenuEntity(0, "Feladatok", "", false))
        extraMenuRepository.save(ExtraMenuEntity(0, "Facebook", "https://facebook.com/xddddddddddd", true))
    }

}
