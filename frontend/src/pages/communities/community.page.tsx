import { Image } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { useParams } from 'react-router-dom'
import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { DataSheet } from './components/DataSheet'
import { Frame } from './components/Frame'
import { useCommunity } from '../../api/hooks/community/useCommunity'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { PageStatus } from '../../common-components/PageStatus'

export default function CommunityPage() {
  const config = useConfigContext()?.components.communities
  const params = useParams()
  const { data, isLoading, isError } = useCommunity(params.id || 'UNKNOWN')
  const breadcrumbItems = [
    {
      title: config?.title,
      to: '/community'
    },
    {
      title: data?.name
    }
  ]

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={config?.title} />

  return (
    <CmschPage>
      <Helmet title={data.name} />
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
