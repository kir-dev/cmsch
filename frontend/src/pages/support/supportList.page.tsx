import { useAuthContext } from '@/api/contexts/auth/useAuthContext'
import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useSupportThreadsQuery } from '@/api/hooks/support/useSupportThreadsQuery'
import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown'
import { PageStatus } from '@/common-components/PageStatus'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { stringifyTimeStamp } from '@/util/core-functions.util'
import { AbsolutePaths } from '@/util/paths'
import { SupportThreadStatus } from '@/util/views/support.view'
import { Link } from 'react-router'

const SupportListPage = () => {
  const config = useConfigContext()
  const support = config?.components?.support
  const title = support?.siteTitle ?? 'Ügyfélszolgálat'
  const buttonLabel = support?.newThreadButtonLabel ?? 'Új üzenet'

  const { isLoggedIn } = useAuthContext()
  const { isLoading, isError, data } = useSupportThreadsQuery(isLoggedIn)

  const statusLabel: Record<string, string> = {
    [SupportThreadStatus.WAITING_FOR_ADMIN]: `Rendező válaszára vár`,
    [SupportThreadStatus.WAITING_FOR_CUSTOMER]: 'A te válaszodra vár',
    [SupportThreadStatus.DONE]: 'Lezárva'
  }

  return (
    <CmschPage title={title}>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-4xl font-bold tracking-tight">{title}</h1>
        <Button asChild>
          <Link to={`${AbsolutePaths.SUPPORT}/new`}>{buttonLabel}</Link>
        </Button>
      </div>

      {support?.topMessage && (
        <div className="mb-6">
          <Markdown text={support.topMessage} />
        </div>
      )}

      {!isLoggedIn ? (
        <p className="text-muted-foreground">
          Bejelentkezés után láthatod az eddigi üzenetváltásaidat. Bejelentkezés nélkül is indíthatsz új üzenetet.
        </p>
      ) : isLoading || isError || !data ? (
        <PageStatus isLoading={isLoading} isError={isError} title={title} />
      ) : data.threads.length === 0 ? (
        <p className="text-muted-foreground">Még nincsenek üzenetváltásaid.</p>
      ) : (
        <div className="flex flex-col gap-3">
          {data.threads.map((thread) => (
            <Link
              key={thread.uuid}
              to={`${AbsolutePaths.SUPPORT}/${thread.uuid}?secret=${thread.uuidSecret}`}
              className="block rounded-lg border p-4 hover:bg-accent transition-colors"
            >
              <div className="flex items-center justify-between gap-2 flex-wrap">
                <span className="font-medium">{thread.title}</span>
                <Badge
                  variant={
                    thread.status === SupportThreadStatus.DONE
                      ? 'outline'
                      : thread.status === SupportThreadStatus.WAITING_FOR_CUSTOMER
                        ? 'default'
                        : 'secondary'
                  }
                >
                  {statusLabel[thread.status]}
                </Badge>
              </div>
              <p className="text-sm text-muted-foreground mt-1">{stringifyTimeStamp(thread.updatedAt)}</p>
            </Link>
          ))}
        </div>
      )}
    </CmschPage>
  )
}

export default SupportListPage
