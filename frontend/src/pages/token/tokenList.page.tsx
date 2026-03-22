import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useTokensQuery } from '@/api/hooks/token/useTokensQuery'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { LinkButton } from '@/common-components/LinkButton'
import { PageStatus } from '@/common-components/PageStatus'
import { PresenceAlert } from '@/common-components/PresenceAlert'
import { Progress } from '@/components/ui/progress'
import { l } from '@/util/language'
import { AbsolutePaths } from '@/util/paths'
import { QrCode } from 'lucide-react'
import { StampComponent } from './components/StampComponent'

const TokenList = () => {
  const { data, isLoading, isError } = useTokensQuery()
  const component = useConfigContext()?.components?.token

  const calculate_progress = (acquired: number, total: number) => (total == 0 ? 100 : (100 * acquired) / total)

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  return (
    <CmschPage loginRequired={true} title={component.title || 'QR kódok'}>
      <h1 className="text-3xl font-bold font-heading mt-5">{component.title || 'QR kódok'}</h1>
      <PresenceAlert acquired={data.collectedTokenCount} needed={data.minTokenToComplete} />
      <div className="flex flex-row space-x-2 mt-5">
        <LinkButton variant="default" className="flex items-center gap-2" href={`${AbsolutePaths.TOKEN}/scan`}>
          <QrCode className="h-5 w-5" /> QR kód beolvasása
        </LinkButton>
      </div>

      <h3 className="text-2xl font-bold mt-10">Haladás</h3>
      <h4 className="text-xl font-bold mt-5">
        Eddig beolvasott kódok: {data.collectedTokenCount} / {data.totalTokenCount}
      </h4>
      <Progress className="mt-1 h-2" value={calculate_progress(data.collectedTokenCount, data.totalTokenCount)} />
      {data.tokens.length > 0 ? (
        <>
          <h4 className="text-xl font-bold mt-5">{l('token-completed')}</h4>
          <div className="flex flex-col space-y-5 mt-1">
            {data.tokens.map((token, i) => {
              return <StampComponent key={i} title={token.title} type={token.type} />
            })}
          </div>
        </>
      ) : (
        <h4 className="text-xl font-bold mt-5">{l('token-empty')}</h4>
      )}
    </CmschPage>
  )
}

export default TokenList
