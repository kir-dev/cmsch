import { useConfigContext } from '@/api/contexts/config/ConfigContext.tsx'
import { useTournamentListQuery } from '@/api/hooks/tournament/queries/useTournamentListQuery.ts'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { PageStatus } from '@/common-components/PageStatus'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { AbsolutePaths } from '@/util/paths.ts'

const TournamentListPage = () => {
  const { isLoading, isError, data } = useTournamentListQuery()
  const component = useConfigContext()?.components?.tournament

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />
  return (
    <CmschPage title={component.title ?? 'Versenyek'}>
      <div className="mb-5">
        <h1 className="mb-5 text-4xl font-bold tracking-tight">{component.title}</h1>
        <h2 className="mb-5 text-lg font-medium">{data.length} verseny található.</h2>
      </div>
      <div className="mt-5 space-y-4">
        {(data ?? []).length > 0 ? (
          data.map((tournament) => (
            <article key={tournament.id}>
              <h2 className="text-xl font-semibold">
                <a className="hover:underline" href={`${AbsolutePaths.TOURNAMENTS}/${tournament.id}`}>
                  {tournament.title}
                </a>
              </h2>
              <div>{tournament.description}</div>
            </article>
          ))
        ) : (
          <div>Nincs egyetlen verseny sem.</div>
        )}
      </div>
    </CmschPage>
  )
}

export default TournamentListPage
