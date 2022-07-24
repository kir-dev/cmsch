import { ButtonGroup, Heading, Progress, Stack } from '@chakra-ui/react'
import axios from 'axios'
import { useEffect, useState } from 'react'
import { Helmet } from 'react-helmet'
import { FaQrcode, FaStamp } from 'react-icons/fa'
import { ProfileView } from '../../util/views/profile.view'
import { TokenView } from '../../util/views/token.view'
import { Loading } from '../../common-components/Loading'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { PresenceAlert } from '../../common-components/PresenceAlert'
import { Paragraph } from '../../common-components/Paragraph'
import { LinkButton } from '../../common-components/LinkButton'
import { StampComponent } from './components/StampComponent'
import { AbsolutePaths } from '../../util/paths'

interface TokenProgress {
  totalTokenCount: number
  minTokenToComplete: number
  acquiredTokenCount: number
  tokens: TokenView[]
  groupName: string
}

const TokenList = () => {
  const [progress, setProgress] = useState<TokenProgress>({
    totalTokenCount: 0,
    minTokenToComplete: 0,
    acquiredTokenCount: 0,
    tokens: [],
    groupName: ''
  })
  const [loading, setLoading] = useState<boolean>(false)

  useEffect(() => {
    setLoading(true)
    axios
      .get(`/api/profile`)
      .then((res) => {
        const profile = res.data as ProfileView
        setProgress({
          tokens: profile.tokens,
          minTokenToComplete: profile.minTokenToComplete,
          totalTokenCount: profile.totalTokenCount,
          acquiredTokenCount: profile.collectedTokenCount,
          groupName: profile.groupName
        })
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
      <Helmet title="QR pecsétek" />
      <Heading as="h1">QR kód pecsétek</Heading>
      <PresenceAlert acquired={progress.acquiredTokenCount} needed={progress.minTokenToComplete} />
      <Paragraph>
        A standoknál végzett aktív tevékenyégért QR kódokat lehet beolvasni. Ha eleget összegyűjt, beválthatja egy tanköri jelenlétre.
      </Paragraph>

      <ButtonGroup mt="5">
        <LinkButton colorScheme="brand" leftIcon={<FaQrcode />} href={`${AbsolutePaths.TOKEN}/scan`}>
          QR kód beolvasása
        </LinkButton>
        {progress?.groupName === 'Kiállító' && (
          <LinkButton colorScheme="brand" leftIcon={<FaStamp />} href="/control/stamps" external newTab={false}>
            Pecsét statisztika
          </LinkButton>
        )}
      </ButtonGroup>

      <Heading as="h3" mt="10" size="lg">
        Haladás
      </Heading>
      <Heading as="h4" size="md" mt={5}>
        Eddig beolvasott kódok: {progress.acquiredTokenCount} / {progress.totalTokenCount}
      </Heading>
      <Progress hasStripe colorScheme="brand" mt="1" value={calculate_progress(progress.acquiredTokenCount, progress.totalTokenCount)} />
      {progress.tokens.length > 0 ? (
        <>
          <Heading as="h4" size="md" mt="5">
            Ahol eddig járt
          </Heading>
          <Stack spacing="5" mt="1">
            {progress.tokens.map((token, i) => {
              return <StampComponent key={i} title={token.title} type={token.type} />
            })}
          </Stack>
        </>
      ) : (
        <Heading as="h4" size="md" mt="5">
          Még nem szerzett pecsétet
        </Heading>
      )}
    </CmschPage>
  )
}

export default TokenList
