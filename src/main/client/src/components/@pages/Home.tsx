import { Alert, AlertIcon, ButtonGroup, Heading, Stack } from '@chakra-ui/react'
import { Paragraph } from '../@commons/Basics'
import { Page } from '../@layout/Page'
import React from 'react'
import { LinkButton } from '../@commons/LinkButton'
import { ImageCarousel } from '../@commons/ImageCarousel'
import { Schedule } from '../@commons/Schedule'
import { EVENTS } from '../../content/events'

export const Home: React.FC = () => {
  return (
    <Page>
      <Heading size="3xl" textAlign="center" marginTop={10}>
        Üdvözlünk a{' '}
        <Heading as="span" color="brand.500" size="3xl">
          GólyaKörTe
        </Heading>{' '}
        portálon
      </Heading>
      <Paragraph marginTop={20} color="gray.500" textAlign="center" maxW={800} marginX="auto">
        Elérkezett a GólyaKörTe! Ezen az oldalon tájékozódhatsz az eseményről. A rendezvény alatt feladatokat tudsz megoldani nyereményekért
        és tanköri aláírásért!
      </Paragraph>
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
      <Heading as="h2" size="lg" textAlign="center" marginTop={20}>
        Események
      </Heading>
      <Alert marginTop={10} variant="left-accent" width="fit-content" marginX="auto">
        <AlertIcon />A változás jogát fenntartjuk! Kísérd figyelemmel az oldal tetején megjelenő értesítéseket!
      </Alert>
      <Schedule events={EVENTS} />
      <Heading as="h2" size="lg" textAlign="center" marginTop={20}>
        Térképek
      </Heading>
      <ImageCarousel images={['/img/maps/bmei.png', '/img/maps/sch.png', '/img/maps/fnt.png']} />
    </Page>
  )
}
