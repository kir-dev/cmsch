import { Page } from '../@layout/Page'
import React from 'react'
import { Heading, Button, Progress, Stack, Icon, Center, Flex, Box, Stat, StatHelpText, StatLabel } from '@chakra-ui/react'
import { FaQrcode, FaStamp } from 'react-icons/fa'
import { TokenDTO } from 'types/dto/token'
import { Paragraph } from 'components/@commons/Basics'

export const QRList: React.FC = (props) => {
  //Mock data for tokens
  const token_response: TokenDTO[] = [
    { title: 'Mock Token 1', type: 'KÖR' },
    { title: 'Kir-Dev token of awesomeness', type: 'KÖR' },
    { title: 'Úristen ez a title very big. Very Very Big', type: 'KÖR' }
  ]

  return (
    <Page {...props}>
      <Heading>QR kód vadászat</Heading>
      <Paragraph>
        A standoknál végzett aktív tevékenyégért QR kódokat lehet gyűjteni, amiket tanköri hiányzások igazolására lehet beváltani. TODO:
        Fogalmazzunk meg ide szöveget, mert ma bevettem a fogalmazás gátlóm.
      </Paragraph>

      <Button colorScheme={'brand'} leftIcon={<FaQrcode />}>
        QR kód beolvasása
      </Button>

      <Heading>Haladás</Heading>
      <Heading as="h4" size="md">
        Eddig beolvasott tokenek: {token_response.length} / 5
      </Heading>

      <Progress hasStripe colorScheme="brand" value={(100 * token_response.length) / 5} />
      <Heading as="h4" size="md">
        Amhol eddig jártál
      </Heading>
      <Stack spacing="5" alignItems={'center'}>
        {token_response.map((token, i) => {
          return (
            <Box key={i} maxW={'md'} minW={'md'} borderWidth="1px" borderRadius={'lg'} overflow={'hidden'} boxShadow="lg" bg="brand.100">
              <Flex>
                <Center bg={'brand.300'} padding="2">
                  <Icon as={FaStamp} boxSize="2em" fontSize="3xl" />
                </Center>
                <Center width="100%" paddingStart={'3'} textAlign="center">
                  <Stat>
                    <StatLabel fontSize="xl">{token.title}</StatLabel>
                    <StatHelpText>{token.type}</StatHelpText>
                  </Stat>
                </Center>
              </Flex>
            </Box>
          )
        })}
      </Stack>
    </Page>
  )
}
