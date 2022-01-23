import { Page } from '../@layout/Page'
import React, { useEffect, useState } from 'react'
import { ButtonGroup, Heading, Progress, Stack } from '@chakra-ui/react'
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
  const [loading, setLoading] = useState<boolean>(false)

  useEffect(() => {
    setLoading(true)
    axios
      .get(`${API_BASE_URL}/api/profile`)
      .then((res) => {
        const profile = res.data as ProfileDTO
        setTokens(profile.tokens || [])
        setTotalTokenCount(profile.totalTokenCount || 0)
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

  if (loading) return <Loading />

  return (
    <Page {...props} loginRequired>
      <Heading>QR kód vadászat</Heading>
      <Paragraph>
        A standoknál végzett aktív tevékenyégért QR kódokat lehet gyűjteni, amiket tanköri hiányzások igazolására lehet beváltani. TODO:
        Fogalmazzunk meg ide szöveget, mert ma bevettem a fogalmazás gátlóm.
      </Paragraph>

      <ButtonGroup>
        <LinkButton colorScheme="brand" leftIcon={<FaQrcode />} href="/qr/scan">
          QR kód beolvasása
        </LinkButton>
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
          return <StampComponent key={i} title={token.title} type={token.type} />
        })}
      </Stack>
    </Page>
  )
}
