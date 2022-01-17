import { Page } from '../@layout/Page'
import React from 'react'
import { Heading, Button, Progress, Stack } from '@chakra-ui/react'
import { FaQrcode } from 'react-icons/fa'
import { TokenDTO } from 'types/dto/token'
import { Paragraph } from 'components/@commons/Basics'
import { useNavigate } from 'react-router-dom'
import { StampComponent } from 'components/@commons/StampComponent'

export const QRList: React.FC = (props) => {
  //Mock data for tokens
  const token_response: TokenDTO[] = [
    { title: 'Mock Token 1', type: 'KÖR' },
    { title: 'Kir-Dev token of awesomeness', type: 'KÖR' },
    { title: 'Úristen ez a title very big. Very Very Big', type: 'KÖR' }
  ]

  const navigate = useNavigate()

  const scanEventHandler = () => {
    navigate('/qr/scan')
  }

  return (
    <Page {...props}>
      <Heading>QR kód vadászat</Heading>
      <Paragraph>
        A standoknál végzett aktív tevékenyégért QR kódokat lehet gyűjteni, amiket tanköri hiányzások igazolására lehet beváltani. TODO:
        Fogalmazzunk meg ide szöveget, mert ma bevettem a fogalmazás gátlóm.
      </Paragraph>

      <Button colorScheme={'brand'} leftIcon={<FaQrcode />} onClick={scanEventHandler}>
        QR kód beolvasása
      </Button>

      <Heading>Haladás</Heading>
      <Heading as="h4" size="md">
        Eddig beolvasott tokenek: {token_response.length} / 5
      </Heading>

      <Progress hasStripe colorScheme="brand" value={(100 * token_response.length) / 5} />
      <Heading as="h4" size="md">
        Ahol eddig jártál
      </Heading>
      <Stack spacing="5" alignItems={'center'}>
        {token_response.map((token, i) => {
          return <StampComponent key={i} title={token.title} type={token.type}></StampComponent>
        })}
      </Stack>
    </Page>
  )
}
