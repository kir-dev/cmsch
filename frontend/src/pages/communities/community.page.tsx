import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useCommunity } from '@/api/hooks/community/useCommunity'
import { CustomBreadcrumb } from '@/common-components/CustomBreadcrumb'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { PageStatus } from '@/common-components/PageStatus'
import { useParams } from 'react-router'
import { DataSheet } from './components/DataSheet'
import { Frame } from './components/Frame'

export default function CommunityPage() {
  const communities = useConfigContext()?.components?.communities
  const params = useParams()
  const { data, isLoading, isError } = useCommunity(params.id || 'UNKNOWN')
  const breadcrumbItems = [
    {
      title: communities?.title,
      to: '/community'
    },
    {
      title: data?.name
    }
  ]

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={communities?.title} />

  return (
    <CmschPage title={data.name}>
      <CustomBreadcrumb items={breadcrumbItems} className="mt-5" />
      <DataSheet organization={data} />
      {data.videoIds?.map((id) => (
        <Frame key={id} id={id} />
      ))}
      {data.imageIds?.map((url) => (
        <img key={url} className="mt-10 w-full h-auto rounded-lg" src={url} alt="Körkép" />
      ))}
    </CmschPage>
  )
}
