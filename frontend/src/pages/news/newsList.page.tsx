import { Grid, Heading, Text, Box, GridItem, Divider } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useNewsListQuery } from '../../api/hooks/useNewsListQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Link, Navigate } from 'react-router-dom'
import { stringifyTimeStamp } from '../../util/core-functions.util'
import { NewsPreviewDTO } from '../../util/views/news.view'

const NewsListPage = () => {
  const newsList = useNewsListQuery(() => console.log('News list query failed!'))
  const { sendMessage } = useServiceContext()

  if (newsList.isSuccess) {
    return (
      <CmschPage>
        <Helmet title="Hírek" />
        <Heading>Hírek</Heading>
        <Text>{newsList.data.warningMessage}</Text>
        <Grid templateColumns={{ base: 'repeat(1, auto)', md: 'repeat(2, auto)' }} gap={10} marginTop={10}>
          {newsList.data.news.map((n: NewsPreviewDTO, i) => (
            <GridItem key={i}>
              <Link to={'/hirek/' + i}>
                <Heading size="md" mt={'3rem'} mb={'0.5rem'} as={n.highlighted ? 'mark' : 'text'}>
                  {n.title}
                </Heading>
                <Text>{stringifyTimeStamp(n.timestamp)}</Text>//TODO brief content?
              </Link>
              <Divider />
            </GridItem>
          ))}
        </Grid>
      </CmschPage>
    )
  } else {
    sendMessage('Hírek betöltése sikertelen!')
    return <Navigate replace to="/error" />
  }
}

export default NewsListPage
