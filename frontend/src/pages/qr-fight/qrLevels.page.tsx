import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useQrLevelsQuery } from '@/api/hooks/qr/useQrLevelsQuery'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { LinkButton } from '@/common-components/LinkButton'
import Markdown from '@/common-components/Markdown'
import { PageStatus } from '@/common-components/PageStatus'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { AbsolutePaths } from '@/util/paths'
import { QrCode } from 'lucide-react'
import { DataDisplayWrapper } from './components/DataDisplayWrapper'
import { TreasureDataDisplayWrapper } from './components/TreasureDataDisplayWrapper.tsx'

export default function QrLevelsPage() {
  const component = useConfigContext()?.components?.qrFight
  const { data, isLoading, isError } = useQrLevelsQuery()

  if (!component || !component.enabled) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  return (
    <CmschPage title={component.title}>
      <div className="flex items-baseline justify-between flex-wrap">
        <h1 className="text-3xl font-bold font-heading mt-5">{component.title}</h1>
        <LinkButton className="my-5" href={`${AbsolutePaths.TOKEN}/scan`}>
          <QrCode className="mr-2 h-4 w-4" /> QR kód beolvasása
        </LinkButton>
      </div>
      <Markdown text={component.topMessage} />
      <Tabs defaultValue="main" className="mt-10 w-full">
        <TabsList className="grid w-full grid-cols-3">
          <TabsTrigger value="main">Fő szintek</TabsTrigger>
          <TabsTrigger value="extra">Extra szintek</TabsTrigger>
          <TabsTrigger value="tour">Tour de QR</TabsTrigger>
        </TabsList>
        <TabsContent value="main" className="px-0">
          {data.mainLevels.map((a) => (
            <DataDisplayWrapper level={a} key={a.name} />
          ))}
        </TabsContent>
        <TabsContent value="extra" className="px-0">
          {data.extraLevels.map((a) => (
            <DataDisplayWrapper level={a} key={a.name} />
          ))}
        </TabsContent>
        <TabsContent value="tour">
          {data.treasureHuntLevels.map((a) => (
            <TreasureDataDisplayWrapper level={a} key={a.name} />
          ))}
        </TabsContent>
      </Tabs>
    </CmschPage>
  )
}
