import { Heading, Text, Wrap } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Paragraph } from '../../common-components/Paragraph'

const ImpressumPage = () => (
  <CmschPage>
    <Helmet title="Impresszum" />
    <Heading>Impresszum</Heading>
    <Paragraph>
      A Gólyakörte 2022 weblapját az SSSL megbízásából a {/*<Link isExternal color={customTheme.colors.kirDev} href={KIRDEV_URL}>*/}
      {/*  kir-dev*/}
      {/*</Link>{' '}*/}
      <Text as="i">speed-run munkacsoport</Text> készítette.
    </Paragraph>
    <Heading as="h2" size="lg" my="5" textAlign="center">
      Fejlesztők
    </Heading>
    <Wrap justify="center">
      {/*{DEVS.map((dev) => (*/}
      {/*  <WrapItem border="2px" borderColor={useColorModeValue('gray.200', 'gray.700')} borderRadius="md" key={dev.name}>*/}
      {/*    <Flex direction="column" align="center" w="20rem" h="20rem">*/}
      {/*      <Text fontSize="2xl">{dev.name}</Text>*/}
      {/*      <Image src={dev.img} h="15rem" fallbackSrc="/img/big_pear_logo.png" />*/}
      {/*      <HStack spacing={2} my={2}>*/}
      {/*        {dev.tags.map((tag) => (*/}
      {/*          <Tag size={'md'} variant="solid" fontWeight="bold" color="white" bgColor={customTheme.colors.kirDev} key={tag}>*/}
      {/*            {tag}*/}
      {/*          </Tag>*/}
      {/*        ))}*/}
      {/*      </HStack>*/}
      {/*    </Flex>*/}
      {/*  </WrapItem>*/}
      {/*))}*/}
    </Wrap>
    <Paragraph>
      Felhasznált technológiák: Kotlin, Spring-boot, Typescript és React. Mint ahogy az összes többi projektünk, ez is{' '}
      {/*<Link isExternal color={customTheme.colors.kirDev} href={GITHUB_ORG_URL}>*/}
      {/*  nyílt forráskódú*/}
      {/*</Link>*/}. Ha kérdésed van vagy érdekel a többi projektünk is, látogass el az{' '}
      {/*<Link isExternal color={customTheme.colors.kirDev} href={KIRDEV_URL}>*/}
      {/*  oldalunkra*/}
      {/*</Link>{' '}*/}
      vagy keress fel minket a kir-dev standnál!
    </Paragraph>
    <Paragraph>Az alkalmazás a KSZK Kubernetes clusterjében fut, köszönjük az erőforrást és a segítséget nekik ezúton is!</Paragraph>
  </CmschPage>
)

export default ImpressumPage
