import { Alert, AlertIcon, Box, ButtonGroup, Flex, Heading, Image, Stack, useColorModeValue, VStack } from '@chakra-ui/react'
import { FC } from 'react'
import { Helmet } from 'react-helmet'
import { FaQuoteLeft, FaQuoteRight } from 'react-icons/fa'
import { MapImage } from 'types/MapImage'
import { EVENTS } from '../../content/events'
import customTheme from '../../utils/customTheme'
import { ImageCarousel } from '../@commons/ImageCarousel'
import { LinkButton } from '../@commons/LinkButton'
import { Paragraph } from '../@commons/Paragraph'
import { Schedule } from '../@commons/Schedule'
import { Page } from '../@layout/Page'

const MAP_IMAGES: MapImage[] = [
  { light: '/img/maps/I_light.png', dark: '/img/maps/I_dark.png' },
  { light: '/img/maps/1_light.png', dark: '/img/maps/1_dark.png' },
  { light: '/img/maps/SCH_light.png', dark: '/img/maps/SCH_dark.png' }
]

export const Home: FC = () => (
  <Page>
    <Helmet />
    <Heading size="3xl" textAlign="center" marginTop={10}>
      Üdvözlünk a{' '}
      <Heading as="span" color={useColorModeValue('brand.500', 'brand.600')} size="3xl">
        GólyaKörTe
      </Heading>{' '}
      portálon
    </Heading>
    <Flex align="center">
      <BlockQuote quoteMarkSize={4}>
        <Paragraph>Kedves Gólyák!</Paragraph>
        <Paragraph>Köszöntünk titeket a 2022-es GólyaKörTe honlapján!</Paragraph>
        <Paragraph>
          A GólyaKörTe egy olyan program sorozat, amelynek segítségével a VIK-es körök bemutatkozhatnak nektek, hogy megismerjétek a
          munkájukat. A rendezvény segíti a közéletben való elhelyezkedést és lehetőséget ad, hogy kipróbáljatok új dolgokat vagy akár a
          meglévő érdeklődéseiteket elmélyítsétek.
        </Paragraph>
        <Paragraph>Biztatunk titeket, hogy vegyetek részt a Gólyakörtén és találjátok meg az új hobbitokat!</Paragraph>
        <Paragraph fontStyle="italic">
          Üdv,
          <br />
          Főrendezők
        </Paragraph>
      </BlockQuote>
      <Image src="/img/big_stork_logo.png" h="30rem" display={{ base: 'none', md: 'block' }} />
    </Flex>
    <Stack justifyContent="center" marginTop={20} direction="column" alignItems="center">
      <LinkButton href="/korok" colorScheme="brand" size="lg">
        Körök listája
      </LinkButton>
      <LinkButton href="/reszortok" colorScheme="brand" size="lg" variant="ghost">
        Reszortok listája
      </LinkButton>
    </Stack>
    <Heading as="h2" size="lg" marginTop={20} textAlign="center">
      Információk
    </Heading>
    <ButtonGroup marginTop={10} justifyContent="center">
      <LinkButton external newTab={false} href="#esemenyek" variant="outline" colorScheme="brand">
        Események
      </LinkButton>
      <LinkButton external newTab={false} href="#terkepek" variant="outline" colorScheme="brand">
        Térképek
      </LinkButton>
    </ButtonGroup>
    <Heading as="h2" size="lg" marginTop={20} textAlign="center">
      Feladatok
    </Heading>
    <Paragraph textAlign="center">Három típusú feladatot tudtok teljesíteni. Ezekhez AuthSch fiók használata szükséges!</Paragraph>
    <ButtonGroup marginTop={10} justifyContent="center">
      <LinkButton href="/riddleok" variant="outline" colorScheme="brand">
        Riddle
      </LinkButton>
      <LinkButton href="/bucketlist" variant="outline" colorScheme="brand">
        Bucketlist
      </LinkButton>
      <LinkButton href="/qr" variant="outline" colorScheme="brand">
        QR pecsétek
      </LinkButton>
    </ButtonGroup>
    <Heading as="h2" size="lg" textAlign="center" marginTop={20} id="esemenyek">
      Események
    </Heading>
    <Alert marginTop={10} variant="left-accent" width="fit-content" marginX="auto">
      <AlertIcon />A változás jogát fenntartjuk! Kísérd figyelemmel az oldal tetején megjelenő értesítéseket!
    </Alert>
    <Schedule events={EVENTS} />
    <Heading as="h2" size="lg" textAlign="center" marginTop={20} id="terkepek">
      Térképek
    </Heading>
    <ImageCarousel images={MAP_IMAGES} />
  </Page>
)

type BlockQuoteProps = {
  quoteMarkSize: number
}

const BlockQuote: FC<BlockQuoteProps> = ({ children, quoteMarkSize }) => (
  <VStack
    px={quoteMarkSize / 2 + 'rem'}
    py={quoteMarkSize + 'rem'}
    marginTop={20}
    color="gray.500"
    maxW={800}
    marginX="auto"
    align="flex-start"
    spacing={5}
    position="relative"
  >
    <QuoteMark side="left" size={quoteMarkSize} />
    {children}
    <QuoteMark side="right" size={quoteMarkSize} />
  </VStack>
)

type QuoteMarkProps = {
  side: 'left' | 'right'
  size: number
}

const QuoteMark: FC<QuoteMarkProps> = ({ side, size }) => (
  <Box
    position="absolute"
    top={side === 'left' ? 0 : undefined}
    bottom={side === 'right' ? 0 : undefined}
    left={side === 'left' ? 0 : undefined}
    right={side === 'right' ? 0 : undefined}
  >
    {side === 'left' ? (
      <FaQuoteLeft size={size + 'rem'} color={useColorModeValue(customTheme.colors.brand['500'], customTheme.colors.brand['600'])} />
    ) : (
      <FaQuoteRight size={size + 'rem'} color={useColorModeValue(customTheme.colors.brand['500'], customTheme.colors.brand['600'])} />
    )}
  </Box>
)
