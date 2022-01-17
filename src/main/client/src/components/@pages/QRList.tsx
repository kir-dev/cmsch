import { Page } from '../@layout/Page'
import React, { useEffect, useState } from 'react'
import { Heading, Button, Progress, Stack, ButtonGroup } from '@chakra-ui/react'
import { FaQrcode } from 'react-icons/fa'
import { TokenDTO } from 'types/dto/token'
import { Paragraph } from 'components/@commons/Basics'
import { useNavigate } from 'react-router-dom'
import { StampComponent } from 'components/@commons/StampComponent'
import axios from 'axios'
import { API_BASE_URL } from 'utils/configurations'
import { ProfileDTO } from 'types/dto/profile'

export const QRList: React.FC = (props) => {
  const [tokens, setTokens] = useState<TokenDTO[]>([])
  const [totalTokenCount, setTotalTokenCount] = useState<number>(0)
  const navigate = useNavigate()

  useEffect(() => {
    axios
      .get(`${API_BASE_URL}/api/profile`)
      .then((res) => {
        const profile = res.data as ProfileDTO
        setTokens(profile.tokens || [])
        setTotalTokenCount(profile.totalTokenCount || 0)
      })
      .catch((err) => {
        console.log(err)
      })
  }, [])

  const scanEventHandler = () => {
    navigate('/qr/scan')
  }

  const calculate_progress = () => {
    if (totalTokenCount == 0) return 100
    else return (100 * tokens.length) / totalTokenCount
  }

  return (
    <Page {...props}>
      <Heading>QR kód vadászat</Heading>
      <Paragraph>
        A standoknál végzett aktív tevékenyégért QR kódokat lehet gyűjteni, amiket tanköri hiányzások igazolására lehet beváltani. TODO:
        Fogalmazzunk meg ide szöveget, mert ma bevettem a fogalmazás gátlóm.
      </Paragraph>

      <ButtonGroup>
        <Button colorScheme={'brand'} leftIcon={<FaQrcode />} onClick={scanEventHandler}>
          QR kód beolvasása
        </Button>
      </ButtonGroup>

      <Heading>Haladás</Heading>
      <Heading as="h4" size="md">
        Eddig beolvasott tokenek: {tokens.length} / {totalTokenCount}
      </Heading>

      <Progress hasStripe colorScheme="brand" value={calculate_progress()} />
      <Heading as="h4" size="md">
        Ahol eddig jártál
      </Heading>
      <Stack spacing="5" alignItems={{ base: 'center', md: 'start' }}>
        {tokens.map((token, i) => {
          return <StampComponent key={i} title={token.title} type={token.type}></StampComponent>
        })}
      </Stack>
    </Page>
  )
}
