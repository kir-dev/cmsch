import { ButtonGroup, Heading, Progress, Stack } from '@chakra-ui/react'
import { FaQrcode } from 'react-icons/fa'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useTokensQuery } from '../../api/hooks/token/useTokensQuery'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import { PageStatus } from '../../common-components/PageStatus'
import { PresenceAlert } from '../../common-components/PresenceAlert'
import { useBrandColor } from '../../util/core-functions.util.ts'
import { l } from '../../util/language'
import { AbsolutePaths } from '../../util/paths'
import { StampComponent } from './components/StampComponent'

const TokenList = () => {
  const { data, isLoading, isError } = useTokensQuery()
  const config = useConfigContext()
  const component = config?.components?.token
  const app = config?.components?.app
  const brandColor = useBrandColor()

  const calculate_progress = (acquired: number, total: number) => (total == 0 ? 100 : (100 * acquired) / total)

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  return (
    <CmschPage loginRequired>
      <title>
        {app?.siteName || 'CMSch'} | {component.title || 'QR kódok'}
      </title>
      <Heading as="h1" variant="main-title">
        {component.title || 'QR kódok'}
      </Heading>
      <PresenceAlert acquired={data.collectedTokenCount} needed={data.minTokenToComplete} />
      <ButtonGroup mt="5">
        <LinkButton colorScheme={brandColor} leftIcon={<FaQrcode />} href={`${AbsolutePaths.TOKEN}/scan`}>
          QR kód beolvasása
        </LinkButton>
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
