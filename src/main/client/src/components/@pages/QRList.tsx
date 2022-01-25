import { Page } from '../@layout/Page'
import React, { useEffect, useState } from 'react'
import { Alert, AlertIcon, ButtonGroup, Heading, Progress, Stack } from '@chakra-ui/react'
import { FaQrcode } from 'react-icons/fa'
import { TokenDTO } from 'types/dto/token'
import { Paragraph } from 'components/@commons/Basics'
import { StampComponent } from 'components/@commons/StampComponent'
import axios from 'axios'
import { API_BASE_URL } from 'utils/configurations'
import { ProfileDTO } from 'types/dto/profile'
import { LinkButton } from '../@commons/LinkButton'
import { Loading } from '../../utils/Loading'
import { useServiceContext } from '../../utils/useServiceContext'

export const QRList: React.FC = (props) => {
  const { throwError } = useServiceContext()

  const [tokens, setTokens] = useState<TokenDTO[]>([])
  const [totalTokenCount, setTotalTokenCount] = useState<number>(0)
  const [minTokenToComplete, setMinTokenToComplete] = useState<number>(0)
  const [loading, setLoading] = useState<boolean>(false)

  useEffect(() => {
    setLoading(true)
    axios
      .get(`${API_BASE_URL}/api/profile`)
      .then((res) => {
        const profile = res.data as ProfileDTO
        setTokens(profile.tokens || [])
        setTotalTokenCount(profile.totalTokenCount || 0)
        setMinTokenToComplete(profile.minTokenToComplete || 0)
        setLoading(false)
      })
      .catch(() => {
        throwError('Nem sikerült lekérni a QR kódokat.')
      })
  }, [])

  const calculate_progress = () => {
    if (totalTokenCount == 0) return 100
    else return (100 * tokens.length) / totalTokenCount
  }

  const alertBar = (acquired: number, needed: number) => {
    if (acquired == null || needed == null) return <></>
    else if (acquired < needed)
      return (
        <Alert variant="left-accent" status="info" mt="5">
          <AlertIcon />
          Még {needed - acquired} darab QR kód kell a tanköri jelenlét megszerzéséig.
        </Alert>
      )
    else
      return (
        <Alert variant="left-accent" status="success" mt="5">
          <AlertIcon />
          Megvan a tanköri jelenlét!
        </Alert>
      )
  }

  if (loading) return <Loading />

  return (
    <Page {...props} loginRequired>
      <Heading as="h1">QR kód vadászat</Heading>
      {alertBar(tokens.length, minTokenToComplete)}
      <Paragraph>
        A standoknál végzett aktív tevékenyégért QR kódokat lehet gyűjteni. Ha eleget összegíűjtesz, beválthatod egy tanköri jelenlétre.
      </Paragraph>

      <ButtonGroup mt="5">
        <LinkButton colorScheme="brand" leftIcon={<FaQrcode />} href="/qr/scan">
          QR kód beolvasása
        </LinkButton>
      </ButtonGroup>

      <Heading as="h3" mt="10">
        Haladás
      </Heading>
      <Heading as="h4" size="md" mt={5}>
        Eddig beolvasott kódok: {tokens.length} / {totalTokenCount}
      </Heading>
      <Progress hasStripe colorScheme="brand" mt="1" value={calculate_progress()} />

      <Heading as="h4" size="md" mt="5">
        Ahol eddig jártál
      </Heading>
      <Stack spacing="5" mt="1">
        {tokens.map((token, i) => {
          return <StampComponent key={i} title={token.title} type={token.type} />
        })}
      </Stack>
    </Page>
  )
}
