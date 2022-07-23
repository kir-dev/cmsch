import axios from 'axios'
import { useState } from 'react'

export const useFormSubmit = (slug: string, onError?: (err: any) => void) => {
  const [loading, setLoading] = useState<boolean>(false)
  const [data, setData] = useState<string>()
  const submit = (formBody: Record<string, unknown>, edit?: boolean) => {
    if (edit) {
      axios
        .put(`/api/form/${slug}`, formBody)
        .then((res) => {
          setData(res.data)
        })
        .catch(onError)
        .finally(() => {
          setLoading(false)
        })
    } else {
      axios
        .post(`/api/form/${slug}`, formBody)
        .then((res) => {
          setData(res.data)
        })
        .catch(onError)
        .finally(() => {
          setLoading(false)
        })
    }
  }
  return { result: data, submit, submitLoading: loading }
}
