import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useKirPayLeaderboardQuery } from '@/api/hooks/kirpay/useKirPayLeaderboardQuery'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { PageStatus } from '@/common-components/PageStatus'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'

const KirPayLeaderboardPage = () => {
  const component = useConfigContext()?.components?.kirpay
  const { data, isError, isLoading } = useKirPayLeaderboardQuery(!!component)

  if (!component) return <ComponentUnavailable />

  const title = component.title || 'Kir-Pay Toplista'

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={title} />

  return (
    <CmschPage title={title}>
      <h1 className="m-8 text-center text-4xl font-bold tracking-tight">{title}</h1>

      {data.entries.length === 0 ? (
        <p className="text-center text-muted-foreground">Még nincs adat a toplistához.</p>
      ) : (
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="w-16">#</TableHead>
              <TableHead>Név</TableHead>
              <TableHead className="text-right">Vásárolt termékek</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {data.entries.map((entry, index) => (
              <TableRow key={entry.name}>
                <TableCell className="font-medium">{index + 1}</TableCell>
                <TableCell>{entry.name}</TableCell>
                <TableCell className="text-right">{entry.itemCount} db</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}
    </CmschPage>
  )
}

export default KirPayLeaderboardPage
