import { Alert, AlertIcon, Box, ButtonGroup, Heading, Stack, VStack } from '@chakra-ui/react'
import { Paragraph } from '../@commons/Basics'
import { Page } from '../@layout/Page'
import React from 'react'
import { LinkButton } from '../@commons/LinkButton'
import { ImageCarousel } from '../@commons/ImageCarousel'
import { Schedule } from '../@commons/Schedule'
import { EVENTS } from '../../content/events'
import { Helmet } from 'react-helmet'
import { FaQuoteLeft, FaQuoteRight } from 'react-icons/fa'
import customTheme from '../../utils/customTheme'

export const Home: React.FC = () => {
  return (
    <Page>
      <Helmet />
      <Heading size="3xl" textAlign="center" marginTop={10}>
        Üdvözlünk a{' '}
        <Heading as="span" color="brand.500" size="3xl">
          GólyaKörTe
        </Heading>{' '}
        portálon
      </Heading>
      <BlockQuote quoteMarkSize={4}>
        <Paragraph>Kedves Gólyák!</Paragraph>
        <Paragraph>Köszöntünk titeket a 2022-es Gólyakörte honlapján!</Paragraph>
        <Paragraph>
          A Gólyakörte egy olyan rendezvény, ahol a VIK-es körök bemutatkoznak nektek, hogy megismerjétek, hogy mivel foglalkoznak. Ez a
          rendezvény segíti a közéletben való elhelyezkedéseteket, megnyitja a lehetőségeiteket, hogy új dolgokat próbáljatok ki vagy, hogy
          a meglévő érdeklődéseiteket elmélyítsétek.
        </Paragraph>
        <Paragraph>Biztatunk titeket, hogy vegyetek részt a rendezvényen és találjátok meg az új hobbitokat!</Paragraph>
        <Paragraph fontStyle="italic">
          Üdv,
          <br />
          Főrendezők
        </Paragraph>
      </BlockQuote>
      <Stack justifyContent="center" marginTop={20} direction="column" alignItems="center">
        <LinkButton href="/korok" colorScheme="brand" size="lg">
          Körök listája
        </LinkButton>
        <LinkButton href="/reszortok" colorScheme="brand" size="lg" variant="ghost">
          Reszortok listája
        </LinkButton>
      </Stack>
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
          QR-kódok
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
      <ImageCarousel images={['/img/maps/bmei.png', '/img/maps/sch.png', '/img/maps/fnt.png']} />
    </Page>
  )
}

type BlockQuoteProps = {
  quoteMarkSize: number
}

const BlockQuote: React.FC<BlockQuoteProps> = ({ children, quoteMarkSize }) => {
  return (
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
}

type QuoteMarkProps = {
  side: 'left' | 'right'
  size: number
}

const QuoteMark: React.FC<QuoteMarkProps> = ({ side, size }) => {
  return (
    <Box
      position="absolute"
      top={side === 'left' ? 0 : undefined}
      bottom={side === 'right' ? 0 : undefined}
      left={side === 'left' ? 0 : undefined}
      right={side === 'right' ? 0 : undefined}
    >
      {side === 'left' ? (
        <FaQuoteLeft size={size + 'rem'} color={customTheme.colors.brand['300']} />
      ) : (
        <FaQuoteRight size={size + 'rem'} color={customTheme.colors.brand['300']} />
      )}
    </Box>
  )
}
