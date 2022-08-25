import { ButtonGroup, Heading, Progress, Stack } from '@chakra-ui/react'
import axios from 'axios'
import { useEffect, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { FaQrcode } from 'react-icons/fa'
import { TokenView } from '../../util/views/token.view'
import { Loading } from '../../common-components/Loading'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { PresenceAlert } from '../../common-components/PresenceAlert'
import { LinkButton } from '../../common-components/LinkButton'
import { StampComponent } from './components/StampComponent'
import { AbsolutePaths } from '../../util/paths'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { l } from '../../util/language'

interface TokenProgress {
  totalTokenCount: number
  collectedTokenCount: number
  minTokenToComplete: number
  tokens: TokenView[]
}

const TokenList = () => {
  const [progress, setProgress] = useState<TokenProgress>({
    totalTokenCount: 0,
    collectedTokenCount: 0,
    minTokenToComplete: 0,
    tokens: []
  })
  const [loading, setLoading] = useState<boolean>(false)
  const config = useConfigContext()

  useEffect(() => {
    setLoading(true)
    axios
      .get(`/api/tokens`)
      .then((res) => {
        setProgress(res.data)
        setLoading(false)
      })
      .catch(() => {
        console.error('Nem sikerült lekérni a QR kódokat.')
      })
  }, [])

  const calculate_progress = (acquired: number, total: number) => (total == 0 ? 100 : (100 * acquired) / total)

  if (loading) return <Loading />

  return (
    <CmschPage loginRequired groupRequired>
      <Helmet title={config?.components.token.title || 'QR kódok'} />
      <Heading as="h1">{config?.components.token.title || 'QR kódok'}</Heading>
      <PresenceAlert acquired={progress.collectedTokenCount} needed={progress.minTokenToComplete} />
      {/* <Paragraph>
        A standoknál végzett aktív tevékenyégért QR kódokat lehet beolvasni. Ha eleget összegyűjt, beválthatja egy tanköri jelenlétre.
      </Paragraph> */}

      <ButtonGroup mt="5">
        <LinkButton colorScheme="brand" leftIcon={<FaQrcode />} href={`${AbsolutePaths.TOKEN}/scan`}>
          QR kód beolvasása
        </LinkButton>
        {/* {progress?.groupName === 'Kiállító' && (
          <LinkButton colorScheme="brand" leftIcon={<FaStamp />} href="/control/stamps" external newTab={false}>
            Pecsét statisztika
          </LinkButton>
        )} */}
      </ButtonGroup>

      <Heading as="h3" mt="10" size="lg">
        Haladás
      </Heading>
      <Heading as="h4" size="md" mt={5}>
        Eddig beolvasott kódok: {progress.collectedTokenCount} / {progress.totalTokenCount}
      </Heading>
      <Progress hasStripe colorScheme="green" mt="1" value={calculate_progress(progress.collectedTokenCount, progress.totalTokenCount)} />
      {progress.tokens.length > 0 ? (
        <>
          <Heading as="h4" size="md" mt="5">
            {l('token-completed')}
          </Heading>
          <Stack spacing="5" mt="1">
            {progress.tokens.map((token, i) => {
              return <StampComponent key={i} title={token.title} type={token.type} />
            })}
          </Stack>
        </>
      ) : (
        <Heading as="h4" size="md" mt="5">
          {l('token-empty')}
        </Heading>
      )}
    </CmschPage>
  )
}

export default TokenList
