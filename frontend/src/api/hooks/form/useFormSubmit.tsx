import axios from 'axios'
import { useState } from 'react'
import { FormSubmitResult } from '../../../util/views/form.view'
import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'

export const useFormSubmit = (slug: string, onError?: (err: any) => void) => {
  const [loading, setLoading] = useState<boolean>(false)
  const [data, setData] = useState<string>()
  const submit = (formBody: Object, edit?: boolean) => {
    if (edit) {
      axios
        .put<FormSubmitResult>(joinPath(ApiPaths.FORM, slug), formBody)
        .then((res) => {
          setData(res.data)
        })
        .catch(onError)
        .finally(() => {
          setLoading(false)
        })
    } else {
      axios
        .post<FormSubmitResult>(joinPath(ApiPaths.FORM, slug), formBody)
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
