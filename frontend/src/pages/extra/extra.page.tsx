import { FunctionComponent } from 'react'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Heading } from '@chakra-ui/react'
import Markdown from '../../common-components/Markdown'
import { Navigate, useParams } from 'react-router-dom'
import { useExtraPage } from '../../api/hooks/useExtraPage'
import { Helmet } from 'react-helmet-async'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { RoleType } from '../../util/views/profile.view'
import { AbsolutePaths } from '../../util/paths'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { l } from '../../util/language'
import { LoadingPage } from '../loading/loading.page'

interface ExtraPageProps {}

const ExtraPage: FunctionComponent<ExtraPageProps> = () => {
  const params = useParams()
  const { profile } = useAuthContext()
  const { data, isLoading, error } = useExtraPage(params.slug || '')
  const { sendMessage } = useServiceContext()

  if (isLoading) {
    return <LoadingPage />
  }

  if (error) {
    sendMessage(l('article-load-failed'))
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (typeof data === 'undefined') {
    sendMessage(l('article-load-failed-contact-developers'))
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (RoleType[data.minRole] > RoleType.GUEST && profile && RoleType[profile.role] < RoleType[data.minRole]) {
    sendMessage(l('no-permission'))
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }
  return (
    <CmschPage>
      <Helmet title={data.title} />
      <Heading>{data.title}</Heading>
      <Markdown text={data.content} />
    </CmschPage>
  )
}

export default ExtraPage
