import { Image } from '@chakra-ui/react'
import { useParams } from 'react-router'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useCommunity } from '../../api/hooks/community/useCommunity'
import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { PageStatus } from '../../common-components/PageStatus'
import { DataSheet } from './components/DataSheet'
import { Frame } from './components/Frame'

export default function CommunityPage() {
  const config = useConfigContext()?.components
  const app = config?.app
  const communities = config?.communities
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
    <CmschPage>
      <title>
        {app?.siteName || 'CMSch'} | {data.name}
      </title>
      <CustomBreadcrumb items={breadcrumbItems} mt={5} />
      <DataSheet organization={data} />
      {data.videoIds?.map((id) => (
        <Frame key={id} id={id} />
      ))}
      {data.imageIds?.map((url) => (
        <Image key={url} marginTop={10} src={url} width="100%" height="auto" alt="Körkép" borderRadius="lg" />
      ))}
    </CmschPage>
  )
}
