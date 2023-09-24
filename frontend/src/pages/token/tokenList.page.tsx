import { ButtonGroup, Heading, Progress, Stack } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { FaQrcode } from 'react-icons/fa'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { PresenceAlert } from '../../common-components/PresenceAlert'
import { LinkButton } from '../../common-components/LinkButton'
import { StampComponent } from './components/StampComponent'
import { AbsolutePaths } from '../../util/paths'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { l } from '../../util/language'
import { useTokensQuery } from '../../api/hooks/token/useTokensQuery'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { PageStatus } from '../../common-components/PageStatus'

const TokenList = () => {
  const { data, isLoading, isError } = useTokensQuery()
  const config = useConfigContext()
  const component = config?.components.token

  const calculate_progress = (acquired: number, total: number) => (total == 0 ? 100 : (100 * acquired) / total)

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  return (
    <CmschPage loginRequired>
      <Helmet title={component.title || 'QR kódok'} />
      <Heading as="h1">{component.title || 'QR kódok'}</Heading>
      <PresenceAlert acquired={data.collectedTokenCount} needed={data.minTokenToComplete} />
      {/* <Paragraph>
        A standoknál végzett aktív tevékenységért QR kódokat lehet beolvasni. Ha eleget összegyűjt, beválthatja egy tanköri jelenlétre.
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
        Eddig beolvasott kódok: {data.collectedTokenCount} / {data.totalTokenCount}
      </Heading>
      <Progress hasStripe colorScheme="green" mt="1" value={calculate_progress(data.collectedTokenCount, data.totalTokenCount)} />
      {data.tokens.length > 0 ? (
        <>
          <Heading as="h4" size="md" mt="5">
            {l('token-completed')}
          </Heading>
          <Stack spacing="5" mt="1">
            {data.tokens.map((token, i) => {
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
