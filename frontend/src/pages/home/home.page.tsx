import { Alert, AlertIcon, Box, Divider, Heading, Text, useToast, VStack } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { useEffect, useMemo } from 'react'
import { Helmet } from 'react-helmet'
import { useLocation } from 'react-router-dom'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { AbsolutePaths } from '../../util/paths'
import Clock from '../countdown/components/clock'
import { Schedule } from './components/Schedule'
import { useEventListQuery } from '../../api/hooks/useEventListQuery'
import { LinkButton } from '../../common-components/LinkButton'

const HomePage = () => {
  const location = useLocation()
  const toast = useToast()
  const { onLogout, onLoginSuccess } = useAuthContext()
  const eventList = useEventListQuery(() => console.log('Event list query failed!'))
  const config = useConfigContext()

  const countTo = useMemo(() => {
    const component = config?.components.countdown
    try {
      if (!component) return new Date()
      return new Date(component?.timeToCountTo * 1000)
    } catch (e) {
      return new Date()
    }
  }, [config?.components.countdown])

  useEffect(() => {
    if (location.pathname === '/logout') {
      onLogout()
    }

    const searchParams = new URLSearchParams(location.search)
    if (searchParams.get('logged-out') == 'true') {
      toast({
        title: 'Kijelentkezés',
        description: 'Sikeres kijelentkeztetés!',
        status: 'success',
        duration: 5000,
        isClosable: true
      })
    }
    if (searchParams.has('jwt')) {
      onLoginSuccess({ jwt: searchParams.get('jwt')!! })
    }
  }, [location])

  const events = useMemo(() => {
    const timestampCorrectedEventList = eventList.data?.map((li) => {
      const { timestampStart, timestampEnd, ...rest } = li
      return { timestampStart: timestampStart * 1000, timestampEnd: timestampEnd * 1000, ...rest }
    })
    return timestampCorrectedEventList?.filter((li) => li.timestampStart > Date.now()).sort((a, b) => a.timestampStart - b.timestampStart)
  }, [eventList.data])

  const eventsToday = events?.filter((ev) => isToday(ev.timestampStart)) || []
  const eventsLater = events?.filter((ev) => !isToday(ev.timestampStart)).slice(0, 3) || []

  return (
    <CmschPage>
      <Helmet />
      <Heading size="3xl" textAlign="center" marginTop={10}>
        Üdvözlünk a{' '}
        <Heading as="span" color={useColorModeValue('brand.500', 'brand.600')} size="3xl">
          {config?.components.app.siteName || 'CMSch'}
        </Heading>{' '}
        portálon
      </Heading>
      {config?.components.countdown?.enabled && (
        <>
          <Heading textAlign="center">{config?.components.countdown?.topMessage}</Heading>
          <Clock countTo={countTo} />
        </>
      )}
      {eventList.data && (
        <VStack>
          <Heading as="h2" size="lg" textAlign="center">
            Események
          </Heading>
          <Alert marginTop={4} variant="left-accent" width="fit-content" marginX="auto">
            <AlertIcon />
            <Box>A változás jogát fenntartjuk! Kísérd figyelemmel az oldal tetején megjelenő értesítéseket!</Box>
          </Alert>
          <VStack spacing={10}>
            <Text textAlign="center" fontSize={25} fontWeight="bolder" marginTop={10}>
              Mai nap
            </Text>
            {eventsToday.length > 0 ? (
              <Schedule events={eventsToday} />
            ) : (
              <Text textAlign="center" color="gray.500" marginTop={10}>
                Nincs több esemény.
              </Text>
            )}
            <Divider />
            <Text textAlign="center" fontSize={25} fontWeight="bolder" marginTop={10}>
              Később
            </Text>
            {eventsLater.length > 0 ? (
              <Schedule verbose events={eventsLater} />
            ) : (
              <Text textAlign="center" color="gray.500" marginTop={10}>
                Nincs több esemény.
              </Text>
            )}
            <LinkButton href={AbsolutePaths.EVENTS}>Részletek</LinkButton>
          </VStack>
        </VStack>
      )}
    </CmschPage>
  )
}

export default HomePage

const isToday = (timeStamp: number) => new Date(timeStamp).toDateString() === new Date().toDateString()
