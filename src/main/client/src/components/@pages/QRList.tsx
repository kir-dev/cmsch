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
import { Helmet } from 'react-helmet'

interface TokenProgress {
  totalTokenCount: number
  minTokenToComplete: number
  acquiredTokenCount: number
  tokens: TokenDTO[]
}

export const QRList: React.FC = (props) => {
  const { throwError } = useServiceContext()
  const [progress, setProgress] = useState<TokenProgress>({
    totalTokenCount: 0,
    minTokenToComplete: 0,
    acquiredTokenCount: 0,
    tokens: []
  })
  const [loading, setLoading] = useState<boolean>(false)

  useEffect(() => {
    setLoading(true)
    axios
      .get(`${API_BASE_URL}/api/profile`)
      .then((res) => {
        const profile = res.data as ProfileDTO
        setProgress({
          tokens: profile.tokens,
          minTokenToComplete: profile.minTokenToComplete,
          totalTokenCount: profile.totalTokenCount,
          acquiredTokenCount: profile.tokens.filter((token) => token.type === 'default').length
        })
        setLoading(false)
      })
      .catch(() => {
        throwError('Nem sikerült lekérni a QR kódokat.')
      })
  }, [])

  const calculate_progress = (acquired: number, total: number) => {
    if (total == 0) return 100
    else return (100 * acquired) / total
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
      <Helmet title="QR page" />
      <Heading as="h1">QR kód vadászat</Heading>
      {alertBar(progress.acquiredTokenCount, progress.minTokenToComplete)}
      <Paragraph>
        A standoknál végzett aktív tevékenyégért QR kódokat lehet beolvasni. Ha eleget összegyűjtesz, beválthatod egy tanköri jelenlétre.
      </Paragraph>

      <ButtonGroup mt="5">
        <LinkButton colorScheme="brand" leftIcon={<FaQrcode />} href="/qr/scan">
          QR kód beolvasása
        </LinkButton>
      </ButtonGroup>

      <Heading as="h3" mt="10" size="lg">
        Haladás
      </Heading>
      <Heading as="h4" size="md" mt={5}>
        Eddig beolvasott kódok: {progress.acquiredTokenCount} / {progress.totalTokenCount}
      </Heading>
      <Progress hasStripe colorScheme="brand" mt="1" value={calculate_progress(progress.acquiredTokenCount, progress.totalTokenCount)} />

      <Heading as="h4" size="md" mt="5">
        Ahol eddig jártál
      </Heading>
      <Stack spacing="5" mt="1">
        {progress.tokens.map((token, i) => {
          return <StampComponent key={i} title={token.title} type={token.type} />
        })}
      </Stack>
    </Page>
  )
}
