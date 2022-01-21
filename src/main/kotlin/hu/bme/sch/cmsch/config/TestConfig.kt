package hu.bme.sch.cmsch.config

import hu.bme.sch.cmsch.dao.*
import hu.bme.sch.cmsch.model.*
import hu.bme.sch.cmsch.service.ProductService
import hu.bme.sch.cmsch.service.UserProfileGeneratorService
import hu.bme.sch.cmsch.util.sha256
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.util.*
import javax.annotation.PostConstruct

const val LOREM_IPSUM_SHORT_1 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas in justo ac arcu placerat posuere eget at purus. Donec porta lorem in semper semper. Phasellus volutpat sapien et ultricies tristique. In ornare libero vel dignissim ultrices."
const val LOREM_IPSUM_SHORT_2 = "Pellentesque non interdum leo. Mauris egestas augue vel lorem dignissim ullamcorper. Suspendisse tempus ex dolor, in sagittis lorem fermentum ut. Morbi dignissim sollicitudin ornare."
const val LOREM_IPSUM_SHORT_3 = "Donec rutrum enim elit, sed facilisis lectus varius sit amet. Ut vel imperdiet ligula. Phasellus id ullamcorper augue, et varius sapien. Nulla suscipit, nibh nec tristique dapibus, lorem libero facilisis mi, sit amet mattis ligula erat vel massa."
const val LOREM_IPSUM_SHORT_4 = "Duis et lacus ac tellus volutpat lobortis. Curabitur ac sapien vel nibh vestibulum congue vel in purus. In eget leo in nisi lacinia lacinia. Sed tempus arcu non mi iaculis lobortis. Vivamus ultricies sed odio sit amet placerat."

const val LOREM_IPSUM_LONG_1 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas in justo ac arcu placerat posuere eget at purus. Donec porta lorem in semper semper. Phasellus volutpat sapien et ultricies tristique. In ornare libero vel dignissim ultrices. Nullam lacus urna, tristique vitae varius at, gravida eu elit. Ut eget velit porta, finibus elit eu, suscipit quam. Pellentesque lobortis lorem at ligula vulputate sagittis. Aliquam id rhoncus turpis. Ut blandit purus vel est finibus, vitae venenatis massa aliquet. Proin eget luctus odio. Ut tincidunt risus non mi dictum malesuada. Sed tempus massa ligula, vel hendrerit diam porttitor nec. Etiam sem orci, fermentum sit amet lorem at, ullamcorper gravida nisi."
const val LOREM_IPSUM_LONG_2 = "Pellentesque non interdum leo. Mauris egestas augue vel lorem dignissim ullamcorper. Suspendisse tempus ex dolor, in sagittis lorem fermentum ut. Morbi dignissim sollicitudin ornare. Praesent vitae leo in nisi maximus sagittis. Nullam scelerisque lectus non magna faucibus, quis dignissim libero iaculis. Integer porta metus ipsum, eu maximus dui semper ut. Nunc mi erat, feugiat nec orci a, vehicula condimentum mi. Praesent sit amet magna vitae velit blandit pulvinar sit amet eget sem. Aliquam erat volutpat. Mauris justo sapien, congue vitae ligula consequat, imperdiet finibus mi. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos."
const val LOREM_IPSUM_LONG_3 = "Donec rutrum enim elit, sed facilisis lectus varius sit amet. Ut vel imperdiet ligula. Phasellus id ullamcorper augue, et varius sapien. Nulla suscipit, nibh nec tristique dapibus, lorem libero facilisis mi, sit amet mattis ligula erat vel massa. Donec laoreet tortor non turpis molestie vestibulum. Suspendisse neque orci, placerat at porta vitae, ultricies ut libero. Quisque vel arcu ac magna interdum rutrum eget vitae velit. In tempus nunc ligula, ac euismod nisl mollis sit amet. Mauris congue auctor enim et venenatis. Maecenas bibendum erat a egestas auctor. Integer quis efficitur ipsum."
const val LOREM_IPSUM_LONG_4 = "Duis et lacus ac tellus volutpat lobortis. Curabitur ac sapien vel nibh vestibulum congue vel in purus. In eget leo in nisi lacinia lacinia. Sed tempus arcu non mi iaculis lobortis. Vivamus ultricies sed odio sit amet placerat. Praesent turpis est, finibus sit amet malesuada vitae, rhoncus et velit. Vestibulum convallis nibh metus, dictum posuere augue facilisis vel. Aliquam viverra mauris sapien, sed pharetra ante ullamcorper quis. Sed scelerisque nisi nibh, rutrum malesuada elit scelerisque ac. Cras rhoncus magna eu risus mattis, vitae eleifend justo lacinia."

const val A_DAY = 60 * 60 * 24

@Profile("test")
@Configuration
class TestConfig(
        private val news: NewsRepository,
        private val events: EventRepository,
        private val achievements: AchievementRepository,
        private val categories: AchievementCategoryRepository,
        private val users: UserRepository,
        private val extraPages: ExtraPageRepository,
        private val groups: GroupRepository,
        private val products: ProductRepository,
        private val profileService: UserProfileGeneratorService,
        private val groupToUserMapping: GroupToUserMappingRepository,
        private val guildToUserMapping: GuildToUserMappingRepository,
        private val submittedAchievements: SubmittedAchievementRepository,
        private val productsService: ProductService,
        private val riddleRepository: RiddleRepository,
        private val riddleCategoryRepository: RiddleCategoryRepository,
        private val tokenRepository: TokenRepository
) {

    private var now = System.currentTimeMillis() / 1000

    @PostConstruct
    fun init() {
        addNews()
        addEvents()
        addGroups()
        addUsers()
        addAchievements()
        addProducts()
        addExtraPages()
        addGroupMapping()
        addGuildMappings()
        addRiddles()
        addTokens()
    }

    private fun addTokens() {
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

    private fun addRiddles() {
        riddleCategoryRepository.save(RiddleCategoryEntity(
            0,
            "Álalános",
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


        riddleRepository.save(RiddleEntity(
            0,
            "Kir-Dev riddle",
            "/image4.png",
            "Kir-Dev",
            "Beszoptad tesóm",
            100,
            1,
            1
        ))
        riddleRepository.save(RiddleEntity(
            0,
            "Admin riddle",
            "/image5.png",
            "Admin",
            "Admin Riddle",
            200,
            1,
            6
        ))
    }

    private fun addGroups() {
        groups.save(GroupEntity(
                name = "V10",
                major = MajorType.EE,
                staff1 = "Kelep Elek| fb.com/kelep| +36301002000",
                staff2 = "Kelep Kelep| fb.com/dzsinks142| +36301002001",
                staff3 = "Elek Elek| fb.com/elek| +36301002002",
                staff4 = "Het Golya| fb.com/nem| +36301002003",
                races = true
        ))

        groups.save(GroupEntity(
                name = "I16",
                major = MajorType.IT,
                staff1 = "Tony Stork| fb.com/kelep| +36301003000",
                staff2 = "Tony Montana| fb.com/dzsinks142| +36301003001",
                staff3 = "Tony Hilfiger| fb.com/elek| +36301003002",
                staff4 = "Bekő Tony| fb.com/nem| +36301003003",
                races = true
        ))

        groups.save(GroupEntity(
                name = "I09",
                major = MajorType.IT,
                staff1 = "Nem Mindegy| fb.com/kelep| +36301003000",
                staff2 = "De Mindegy| fb.com/dzsinks142| +36301003001",
                staff3 = "Tony Hilfiger| fb.com/elek| +36301003002",
                staff4 = "Bekő Tony| fb.com/nem| +36301003003",
                races = true
        ))
    }

    private fun addNews() {
        news.save(NewsEntity(title = "Az eslő hír",
                brief = LOREM_IPSUM_SHORT_1, content = LOREM_IPSUM_LONG_1,
                visible = true, highlighted = false))
        news.save(NewsEntity(title = "A második highlightolt hír",
                brief = LOREM_IPSUM_SHORT_2, content = LOREM_IPSUM_LONG_2,
                visible = true, highlighted = true))
        news.save(NewsEntity(title = "Ez nem is hír, nem látszik",
                brief = LOREM_IPSUM_SHORT_3, content = LOREM_IPSUM_LONG_3,
                visible = false, highlighted = false))
        news.save(NewsEntity(title = "Teszt hír 4",
                brief = LOREM_IPSUM_SHORT_4, content = LOREM_IPSUM_LONG_4,
                visible = true, highlighted = false))
        news.save(NewsEntity(title = "Teszt hír 5",
                brief = LOREM_IPSUM_SHORT_3, content = LOREM_IPSUM_LONG_3,
                visible = true, highlighted = false))
        news.save(NewsEntity(title = "Teszt hír 6",
                brief = LOREM_IPSUM_SHORT_3, content = LOREM_IPSUM_LONG_3,
                visible = true, highlighted = false))
    }

    private fun addEvents() {
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

    private fun addAchievements() {
        val achi1 = AchievementEntity(
                title = "Merre van balra?",
                expectedResultDescription = "Egy kép arról mere van balra",
                categoryId = 1,
                visible = true,
                order = 1,
                availableFrom = now - (12 * A_DAY),
                availableTo = now + (4 * A_DAY),
                type = AchievementType.TEXT,
                maxScore = 50,
                description = LOREM_IPSUM_LONG_1
        )
        achievements.save(achi1)

        val achi2 = AchievementEntity(
                title = "Milyen 'Jé' a kamion?",
                expectedResultDescription = "Egy fotó a kamilyonról",
                categoryId = 2,
                visible = true,
                order = 2,
                availableFrom = now - (3 * A_DAY),
                availableTo = now + (2 * A_DAY),
                type = AchievementType.IMAGE,
                maxScore = 150,
                description = LOREM_IPSUM_LONG_2
        )
        achievements.save(achi2)

        val achi3 = AchievementEntity(
                title = "Valami vicces megjegyzés az egyik gólyalányról",
                expectedResultDescription = "Milyen szinű és miért kék?",
                categoryId = 1,
                visible = false,
                order = 3,
                availableFrom = now - (3 * A_DAY),
                availableTo = now + (2 * A_DAY),
                type = AchievementType.TEXT,
                maxScore = 69,
                description = "Úgy sem látszik"
        )
        achievements.save(achi3)

        achievements.save(AchievementEntity(
                title = "Milyen lóról nevezték el a lóvagtermet?",
                expectedResultDescription = "A ló neve kisbetűvel",
                categoryId = 2,
                visible = true,
                order = 3,
                availableFrom = now - (3 * A_DAY),
                availableTo = now + (2 * A_DAY),
                type = AchievementType.BOTH,
                maxScore = 30,
                description = LOREM_IPSUM_LONG_3
        ))

        achievements.save(AchievementEntity(
                title = "Mingyá' lejár",
                expectedResultDescription = "Kép a centrifugáról (am ez lejárt)",
                categoryId = 3,
                visible = true,
                order = 2,
                availableFrom = now - (3 * A_DAY),
                availableTo = now - (2 * A_DAY),
                type = AchievementType.IMAGE,
                maxScore = 420,
                description = "Ez lejárt"
        ))

        val achi4 = AchievementEntity(
                title = "Mit mér a mérnök?",
                expectedResultDescription = "asszem sört, na mérjetek sört",
                categoryId = 1,
                visible = true,
                order = 4,
                availableFrom = now - (3 * A_DAY),
                availableTo = now + (2 * A_DAY),
                type = AchievementType.IMAGE,
                maxScore = 150,
                description = LOREM_IPSUM_LONG_4
        )
        achievements.save(achi4)


        achievements.save(AchievementEntity(
                title = "Milye van a fának?",
                expectedResultDescription = "gráfelméleti tézis",
                categoryId = 2,
                visible = true,
                order = 4,
                availableFrom = now - (3 * A_DAY),
                availableTo = now + (2 * A_DAY),
                type = AchievementType.TEXT,
                maxScore = 150,
                description = "Levele van, vagy egyel több csúcsa mint éle?"
        ))

        val groupI16 = groups.findByName("I16").orElseThrow()
        val groupI09 = groups.findByName("I09").orElseThrow()
        val groupV10 = groups.findByName("V10").orElseThrow()

        submittedAchievements.save(SubmittedAchievementEntity(
                0,
                achi1,
                groupI16.id,
                "I16",
                null,
                "",
                2,
                "Ezt adtuk be xd",
                "",
                "Hát kár volt bazdmeg",
                false,
                true,
                0
        ))

        submittedAchievements.save(SubmittedAchievementEntity(
                0,
                achi1,
                groupI09.id,
                "I09",
                null,
                "",
                1,
                "Szia Lajos!",
                "",
                "Szia Bazdmeg!",
                true,
                false,
                20
        ))

        submittedAchievements.save(SubmittedAchievementEntity(
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
                false,
                false,
                0
        ))

        submittedAchievements.save(SubmittedAchievementEntity(
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
                false,
                false,
                0
        ))

        submittedAchievements.save(SubmittedAchievementEntity(
                0,
                achi2,
                groupI09.id,
                "I09",
                null,
                "",
                1,
                "",
                "achievements/test.png",
                "",
                false,
                false,
                0
        ))

        categories.save(AchievementCategoryEntity(0, "Mine Category", 1, 0, 3000000000))
        categories.save(AchievementCategoryEntity(
                name = "Mine Category2",
                categoryId = 2,
                availableFrom = 0,
                availableTo = 3000000000
        ))
        categories.save(AchievementCategoryEntity(
            name = "Mine Category3",
            categoryId = 3,
            availableFrom = 0,
            availableTo = 3000000000
        ))
    }

    private fun addUsers() {
        val u1 = UserEntity(
                pekId = UUID.randomUUID().toString(),
                neptun = "HITMAN",
                email = "hitman@beme.hu",
                major = MajorType.EE,
                role = RoleType.BASIC,
                fullName = "Hitman János",
                guild = GuildType.YELLOW,
                groupName = "V10",
                group = groups.findByName("V10").orElse(null)
        )
        profileService.generateProfileForUser(u1)
        users.save(u1)

        val u2 = UserEntity(
                pekId = UUID.randomUUID().toString(),
                neptun = "BATMAN",
                email = "batman@beme.hu",
                major = MajorType.IT,
                role = RoleType.BASIC,
                fullName = "Bat Man",
                guild = GuildType.RED,
                groupName = "V10",
                group = groups.findByName("V10").orElse(null)
        )
        profileService.generateProfileForUser(u2)
        users.save(u2)

        val u3 = UserEntity(
                pekId = UUID.randomUUID().toString(),
                neptun = "FITYMA",
                email = "fityma@beme.hu",
                major = MajorType.BPROF,
                role = RoleType.BASIC,
                fullName = "Fitty Mátyás",
                guild = GuildType.BLACK
        )
        profileService.generateProfileForUser(u3)
        users.save(u3)
    }

    private fun addProducts() {
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

        productsService.sellProductByG7Id(product1.id, merchant, user1.g7id)
        productsService.sellProductByNeptun(product2.id, merchant, user1.neptun)
        productsService.sellProductByG7Id(product1.id, merchant, user2.g7id)
        productsService.sellProductByNeptun(product1.id, merchant, user2.neptun)
        productsService.sellProductByG7Id(product1.id, merchant, user2.g7id)
    }

    private fun addExtraPages() {
        extraPages.save(ExtraPageEntity(
                title = "Gyakran Ismételt Kérdések",
                url = "gyik",
                visible = true,
                open = true,
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
                        "Na ezt most ne, pls!\n"
        ))

        extraPages.save(ExtraPageEntity(
                title = "Egy másik nemzedék\n",
                url = "egy-masik-nemzedek",
                visible = false,
                open = true,
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

        extraPages.save(ExtraPageEntity(
                title = "Az idei G7 költségvetése",
                url = "koltsegvetes",
                visible = false,
                open = false,
                content = "Az idei G7 költésgvetése\n" +
                        "===\n" +
                        "\n" +
                        "Ja persze, majd ideírjuk...\n"
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

}
