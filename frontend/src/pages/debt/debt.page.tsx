import { useConfigContext } from '@/api/contexts/config/ConfigContext.tsx'
import { useDebtQuery } from '@/api/hooks/debt/useDebtQuery.ts'
import { useProfileQuery } from '@/api/hooks/profile/useProfileQuery.ts'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable.tsx'
import { CmschPage } from '@/common-components/layout/CmschPage.tsx'
import Markdown from '@/common-components/Markdown.tsx'
import { PageStatus } from '@/common-components/PageStatus.tsx'
import { Separator } from '@/components/ui/separator'
import { ProfileQR } from '../profile/components/ProfileQR.tsx'
import { DebtListItem } from './components/debt-list-item.tsx'

const DebtPage = () => {
  const components = useConfigContext()?.components
  const debtComponent = components?.debt
  const profileComponent = components?.profile
  const profileQuery = useProfileQuery()
  const { data, isError, isLoading } = useDebtQuery()

  if (!debtComponent) return <ComponentUnavailable />

  const title = debtComponent.title || 'Tartozások'

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={title} />

  return (
    <CmschPage title={title}>
      <h1 className="text-3xl font-bold font-heading">{title}</h1>
      {profileQuery.data && !!profileComponent && (
        <>
          <Separator className="my-8 h-1 bg-border" />
          <ProfileQR profile={profileQuery.data} component={profileComponent} />
          <Separator className="my-8 h-1 bg-border" />
        </>
      )}
      {debtComponent.topMessage && (
        <div className="mt-5">
          <Markdown text={debtComponent.topMessage} />
        </div>
      )}
      <div className="flex flex-col space-y-2">
        {data.debts.map((item) => (
          <DebtListItem key={item.product + item.price} item={item} />
        ))}
      </div>
      {data.debts.length === 0 && <p className="text-center mt-10 font-bold">Nincs tranzakciód eddig, szaladj és vegyél valami zurát!</p>}
    </CmschPage>
  )
}

export default DebtPage
