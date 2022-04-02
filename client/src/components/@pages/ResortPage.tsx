import { Image } from '@chakra-ui/react'
import { FC } from 'react'
import { Helmet } from 'react-helmet'
import { Navigate, useParams } from 'react-router-dom'
import { COMMUNITIES } from '../../content/communities'
import { RESORTS } from '../../content/resorts'
import { CardListItem } from '../@commons/CardListItem'
import { CustomBreadcrumb } from '../@commons/CustomBreadcrumb'
import { DataSheet } from '../@commons/DataSheet'
import { Frame } from '../@commons/Frame'
import { Page } from '../@layout/Page'

type ResortPageProps = {}

export const ResortPage: FC<ResortPageProps> = () => {
  const params = useParams()
  const resort = RESORTS.find((r) => r.id === params.name)
  if (!resort) return <Navigate to="/reszortok" replace />
  const breadcrumbItems = [
    {
      title: 'Reszortok',
      to: '/reszortok'
    },
    {
      title: resort.name
    }
  ]
  return (
    <Page>
      <Helmet title={resort.name} />
      <CustomBreadcrumb items={breadcrumbItems} />
      <DataSheet organization={resort} />
      {COMMUNITIES.filter((c) => c.resortId === resort.id).map((community) => (
        <CardListItem key={community.id} data={community} link={'/korok/' + community.id} />
      ))}
      {resort.videoIds?.map((id) => (
        <Frame key={id} id={id} />
      ))}
      {resort.imageIds?.map((url) => (
        <Image key={url} marginTop={10} src={url} width="100%" height="auto" alt="Körkép" borderRadius="lg" />
      ))}
    </Page>
  )
}
